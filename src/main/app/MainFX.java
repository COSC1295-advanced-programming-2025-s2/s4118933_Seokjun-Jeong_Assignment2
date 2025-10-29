package main.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import main.core.*;
import main.core.AdmissionService;
import main.core.CareHome;
import main.core.Roster; // present
import main.model.*;
import main.repo.InMemoryRepository;

public class MainFX extends Application {
    private CareHome home;
    private final AdmissionService admission = new AdmissionService();
    private Stage stage;
    private BorderPane root;
    // Initialize authorization and logging session
    private Session session = new Session();
    private AuthService auth = new AuthService(session);
    private LogService logs = LogService.load(new java.io.File("carehome.log"));
    private PrescriptionService prescriptions = new PrescriptionService();
    private Label statusBar = new Label("Not signed in");

    @Override
    public void start(Stage stage) {
        try {
            java.io.File f = new java.io.File("prescriptions.dat");
            if (f.exists()) {
                try (var in = new java.io.ObjectInputStream(new java.io.FileInputStream(f))) {
                    prescriptions = (main.core.PrescriptionService) in.readObject();
                }
            }
        } catch (Exception ignore) {}

        this.stage = stage;

        // Initialize
        var repo = new InMemoryRepository<Staff,String>(Staff::getId);
        home = new CareHome(repo, new Roster());

        // Testing
        // home.staff().save(new Doctor("D001", "Dr. Kim", "drkim", "1234"));
        // home.staff().save(new Nurse("N001", "Nurse Lee", "nlee", "abcd"));

        // layout
        home.addWard(new Ward("W1", new int[]{1,2,4,4,4,3}));
        home.addWard(new Ward("W2", new int[]{2,2,4,4,3,3}));

        root = new BorderPane();
        root.setPadding(new Insets(12));

        HBox wardsBox = new HBox(24);
        wardsBox.setAlignment(Pos.TOP_CENTER);
        for (Ward w : home.wards()) wardsBox.getChildren().add(buildWardPane(w));
        root.setCenter(wardsBox);

        // --- Menubar ---
        MenuBar mb = new MenuBar();

        // Resident
        Menu residentMenu = new Menu("Resident");
        MenuItem add = new MenuItem("Add to vacant bed…");
        add.setOnAction(e -> showAddResidentDialog());
        residentMenu.getItems().addAll(add);

        // Prescription
        Menu presMenu = new Menu("Prescription");
        MenuItem viewPres = new MenuItem("View / Add Prescription…");
        viewPres.setOnAction(e -> showPrescriptionDialog());
        presMenu.getItems().add(viewPres);

        // Account
        Menu account = new Menu("Account");
        MenuItem signIn = new MenuItem("Sign in…");
        signIn.setOnAction(e -> showSignInDialog());

        MenuItem signOut = new MenuItem("Sign out");
        signOut.setOnAction(e -> {
            session.signOut();
            statusBar.setText("Not signed in");
            logs.log("SYSTEM", "SIGN_OUT", "User signed out");
        });

        MenuItem addStaff = new MenuItem("Add staff…");
        addStaff.setOnAction(e -> showAddStaffDialog());

        account.getItems().addAll(signIn, signOut, new SeparatorMenuItem(), addStaff);

        // set MenuBar
        mb.getMenus().addAll(account, residentMenu, presMenu);
        root.setTop(mb);

        // bottom menubar
        statusBar.setPadding(new Insets(6));
        if (statusBar.getText() == null || statusBar.getText().isEmpty()) {
            statusBar.setText("Not signed in");
        }
        root.setBottom(statusBar);

        // show
        stage.setTitle("RMIT Care Home – Phase 2");
        stage.setScene(new Scene(root, 1100, 700));
        stage.show();

        System.out.println("==== ACTION LOGS ====");
        for (var entry : logs.entries()) {
            System.out.println(entry);
        }
        // ToolBar tb = new ToolBar(btnSignIn, btnSignOut);
        // root.setTop(new VBox(mb, tb));
    }


    private void showSignInDialog() {
        Dialog<main.model.Staff> dlg = new Dialog<>();
        dlg.setTitle("Sign In");

        TextField id = new TextField();
        id.setPromptText("Staff ID");

        TextField name = new TextField();
        name.setPromptText("Name");

        ChoiceBox<main.model.Role> role = new ChoiceBox<>();
        role.getItems().addAll(main.model.Role.MANAGER, main.model.Role.DOCTOR, main.model.Role.NURSE);
        role.getSelectionModel().selectFirst();

        GridPane gp = new GridPane();
        gp.setHgap(8);
        gp.setVgap(8);
        gp.addRow(0, new Label("ID"), id);
        gp.addRow(1, new Label("Name"), name);
        gp.addRow(2, new Label("Role"), role);

        dlg.getDialogPane().setContent(gp);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dlg.setResultConverter(bt -> {
            if (bt != ButtonType.OK) return null;
            main.model.Role r = role.getValue();
            switch (r) {
                case MANAGER:
                    return new main.model.Manager(id.getText(), name.getText(), "", "");
                case DOCTOR:
                    return new main.model.Doctor(id.getText(), name.getText(), "", "");
                default:
                    return new main.model.Nurse(id.getText(), name.getText(), "", "");
            }
        });

        dlg.showAndWait().ifPresent(staff -> {
            // logging session setting
            session.signIn(staff);
            statusBar.setText("Signed in: " + staff.getName() + " (" + staff.getRole() + ")");
            logs.log(staff.getId(), "SIGN_IN", staff.getRole().toString());
        });
    }


    private VBox buildWardPane(Ward ward){
        VBox box = new VBox(12);
        box.setPadding(new Insets(8));
        box.setStyle("-fx-border-color:#2a5; -fx-border-radius:8; -fx-border-width:2; -fx-background-color:#f8f8f8;");
        Label title = new Label("Ward " + ward.getWardId().substring(1));
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");
        box.getChildren().add(title);

        GridPane grid = new GridPane();
        grid.setHgap(18); grid.setVgap(18);

        int r=0, c=0;
        for (Room room : ward.getRooms()){
            VBox roomBox = buildRoomBox(room);
            grid.add(roomBox, c, r);
            c++; if (c==2){ c=0; r++; } // 2 x 3 layout
        }
        box.getChildren().add(grid);
        return box;
    }

    private VBox buildRoomBox(Room room){
        VBox roomBox = new VBox(8);
        roomBox.setPadding(new Insets(8));
        roomBox.setStyle("-fx-border-color:#555; -fx-background-color:white;");
        Label roomTitle = new Label(room.getRoomId());
        roomTitle.setStyle("-fx-font-weight:bold;");
        FlowPane bedsPane = new FlowPane(8,8);
        bedsPane.setPrefWrapLength(140);

        for (Bed b : room.getBeds()){
            Button bedBtn = makeBedButton(room, b);
            bedsPane.getChildren().add(bedBtn);
        }
        roomBox.getChildren().addAll(roomTitle, bedsPane);
        return roomBox;
    }

    private Button makeBedButton(Room room, Bed bed){
        Button btn = new Button(bed.getBedId());
        btn.setPrefSize(64, 36);
        refreshBedStyle(btn, bed);

        btn.setOnAction(e -> {
            Resident occ = bed.getOccupant();
            if (occ == null){
                // empty bed → add resident
                showAddResidentDialog(room, bed);
            } else {
                // occupied → show menu
                showOccupiedMenu(room, bed, occ, btn);
            }
        });
        return btn;
    }

    private void refreshBedStyle(Button btn, Bed bed){
        if (!bed.isOccupied()){
            btn.setStyle("-fx-background-color:#ddd; -fx-border-color:#666;");
        } else {
            String color = (bed.getOccupant().getGender()==Gender.MALE) ? "#3b7ddd" : "#d33";
            btn.setStyle("-fx-background-color:"+color+"; -fx-text-fill:white;");
        }
    }

    // “Add to vacant bed…” in whole menu
    private void showAddResidentDialog(){
        Alert info = new Alert(Alert.AlertType.INFORMATION, "빈 침대를 클릭해서 입소할 수 있어요.", ButtonType.OK);
        info.setHeaderText(null); info.showAndWait();
    }

    // add resident in bed
    private void showAddResidentDialog(Room room, Bed bed){
        Dialog<Resident> dlg = new Dialog<>();
        dlg.setTitle("Add Resident → " + bed.getBedId());
        TextField id = new TextField(); id.setPromptText("ID");
        TextField name = new TextField(); name.setPromptText("Name");
        ChoiceBox<Gender> gender = new ChoiceBox<>();
        gender.getItems().addAll(Gender.MALE, Gender.FEMALE);
        gender.getSelectionModel().selectFirst();

        GridPane gp = new GridPane(); gp.setHgap(8); gp.setVgap(8);
        gp.addRow(0, new Label("ID"), id);
        gp.addRow(1, new Label("Name"), name);
        gp.addRow(2, new Label("Gender"), gender);
        dlg.getDialogPane().setContent(gp);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dlg.setResultConverter(bt -> bt==ButtonType.OK ?
                new Resident(id.getText(), name.getText(), gender.getValue()) : null);

        dlg.showAndWait().ifPresent(res -> {
            try{
                admission.assign(res, room, bed);
                // refresh button's color
                refreshAllBeds();
            } catch (RuntimeException ex){
                new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
            }
        });
    }

    private void showOccupiedMenu(Room room, Bed bed, Resident occ, Button btn){
        ContextMenu menu = new ContextMenu();
        MenuItem detail = new MenuItem("View details");
        detail.setOnAction(e -> new Alert(Alert.AlertType.INFORMATION,
                occ.getName()+" ("+occ.getGender()+")\nAdmitted: "+occ.getAdmittedOn(),
                ButtonType.OK).showAndWait());

        MenuItem move = new MenuItem("Move to another bed…");
        move.setOnAction(e -> showMoveDialog(room,bed, occ));

        menu.getItems().addAll(detail, move);
        menu.show(btn, javafx.geometry.Side.RIGHT, 0, 0);
    }

    private void showMoveDialog(Room fromRoom, Bed fromBed, Resident r){
        // move to the first empty bed one
        for (Ward w : home.wards()){
            for (Room room : w.getRooms()){
                for (Bed b : room.getBeds()){
                    if (!b.isOccupied()){
                        try{
                            admission.move(fromBed, room, b, r);
                            fromBed.vacate();
                            refreshAllBeds();
                            new Alert(Alert.AlertType.INFORMATION,
                                    "Moved " + r.getName() + " → " + b.getBedId(), ButtonType.OK).showAndWait();
                            return;
                        } catch (RuntimeException ignore){ /* gender accident */ }
                    }
                }
            }
        }
        new Alert(Alert.AlertType.WARNING, "No suitable vacant bed found.", ButtonType.OK).showAndWait();
    }

    private HBox buildWardsPane() {
        HBox wardsBox = new HBox(24);
        wardsBox.setAlignment(Pos.TOP_CENTER);
        for (Ward w : home.wards()) {
            wardsBox.getChildren().add(buildWardPane(w));
        }
        return wardsBox;
    }

    private void refreshAllBeds(){
        // sketch
        root.setCenter(buildWardsPane());
    }

    private void showAddStaffDialog() {
        try {
            // allow Manager authorization
            auth.require(main.model.Role.MANAGER);
        } catch (RuntimeException ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
            return;
        }

        Dialog<main.model.Staff> dlg = new Dialog<>();
        dlg.setTitle("Add Staff");

        TextField id = new TextField();
        id.setPromptText("ID");

        TextField name = new TextField();
        name.setPromptText("Name");

        TextField password = new TextField();
        password.setPromptText("Password");

        ChoiceBox<main.model.Role> role = new ChoiceBox<>();
        role.getItems().addAll(main.model.Role.MANAGER, main.model.Role.DOCTOR, main.model.Role.NURSE);
        role.getSelectionModel().select(main.model.Role.NURSE);

        GridPane gp = new GridPane();
        gp.setHgap(8);
        gp.setVgap(8);
        gp.addRow(0, new Label("ID"), id);
        gp.addRow(1, new Label("Name"), name);
        gp.addRow(2, new Label("Password"), password);
        gp.addRow(3, new Label("Role"), role);

        dlg.getDialogPane().setContent(gp);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dlg.setResultConverter(bt -> {
            if (bt != ButtonType.OK) return null;
            switch (role.getValue()) {
                case MANAGER:
                    return new main.model.Manager(id.getText(), name.getText(), "", password.getText());
                case DOCTOR:
                    return new main.model.Doctor(id.getText(), name.getText(), "", password.getText());
                default:
                    return new main.model.Nurse(id.getText(), name.getText(), "", password.getText());
            }
        });

        dlg.showAndWait().ifPresent(staff -> {
            home.staff().save(staff);  // save CareHome staff repository
            logs.log(session.current().getId(), "ADD_STAFF",
                    staff.getId() + "(" + staff.getRole() + ")");
            new Alert(Alert.AlertType.INFORMATION,
                    "Staff added: " + staff.getName(),
                    ButtonType.OK).showAndWait();
        });
    }

    private void showPrescriptionDialog() {
        // load the list of patients addmitted to beds
        ChoiceDialog<Resident> dialog = new ChoiceDialog<>();
        for (Ward w : home.wards())
            for (Room r : w.getRooms())
                for (Bed b : r.getBeds())
                    if (b.isOccupied())
                        dialog.getItems().add(b.getOccupant());

        if (dialog.getItems().isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "No residents available.", ButtonType.OK).showAndWait();
            return;
        }

        dialog.setHeaderText("Select a resident to view prescriptions");
        dialog.showAndWait().ifPresent(this::showPrescriptionDetail);
    }

    private void showPrescriptionDetail(Resident res) {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Prescriptions for " + res.getName());

        // List
        ListView<Prescription> list = new ListView<>();
        list.getItems().addAll(prescriptions.getFor(res));

        // Doctor  - add prescription button
        Button addBtn = new Button("Add Prescription");
        addBtn.setOnAction(e -> {
            try {
                auth.require(Role.DOCTOR);
            } catch (RuntimeException ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
                return;
            }

            TextInputDialog m = new TextInputDialog();
            m.setHeaderText("Medicine name");
            m.showAndWait().ifPresent(name -> {
                TextInputDialog d = new TextInputDialog();
                d.setHeaderText("Dose (e.g., 10mg)");
                d.showAndWait().ifPresent(dose -> {
                    TextInputDialog f = new TextInputDialog("Once daily");
                    f.setHeaderText("Frequency");
                    f.showAndWait().ifPresent(freq -> {
                        Prescription p = new Prescription(new Medicine(name, dose), freq, session.current().getId());
                        prescriptions.add(res, p);
                        list.getItems().add(p);
                        logs.log(session.current().getId(), "PRESCRIBE", res.getId() + ": " + p);
                    });
                });
            });
        });

        // Nurse - mark administered button
        Button adminBtn = new Button("Mark Administered");
        adminBtn.setOnAction(e -> {
            try {
                auth.require(Role.NURSE);
            } catch (RuntimeException ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
                return;
            }

            Prescription sel = list.getSelectionModel().getSelectedItem();
            if (sel == null) {
                new Alert(Alert.AlertType.WARNING, "Select a prescription.", ButtonType.OK).showAndWait();
                return;
            }
            if (!sel.isAdministered()) {
                sel.markAdministered();
                list.refresh();
                logs.log(session.current().getId(), "ADMINISTER", res.getId() + ": " + sel.getMedicine());
            }
        });

        HBox actions = new HBox(10, addBtn, adminBtn);
        VBox box = new VBox(8, list, actions);

        dlg.getDialogPane().setContent(box);
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dlg.showAndWait();
    }

    @Override
    public void stop() {
        home.saveToFile(new java.io.File("carehome.dat"));
        logs.save(new java.io.File("carehome.log"));
        try (var out = new java.io.ObjectOutputStream(new java.io.FileOutputStream("prescriptions.dat"))) {
            out.writeObject(prescriptions);
        } catch (Exception ignore) {}
    }

    public static void main(String[] args){ launch(args); }
}

