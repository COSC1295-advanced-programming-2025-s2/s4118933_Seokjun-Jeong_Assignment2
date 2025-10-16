# RMIT HealthCare System

Name: Seokjun Jeong (s4118933)    
Language: Java 17 + JavaFX 25  
IDE: IntelliJ IDEA  

## Overview
A JavaFX-based Resident HealthCare Management System for RMIT Care Home.  
It demonstrates object-oriented design, GUI development, and persistence through phased implementation.

## System Structure
src/
├─ app/ (MainFX.java – GUI entry)
├─ core/ (CareHome, AuthService, LogService, PrescriptionService)
├─ model/ (Resident, Bed, Room, Ward, Staff, Prescription)
├─ repo/ (InMemoryRepository)
└─ test/ (JUnit tests)
 
how to run : Run `MainFX`
## Features by Phase
| Phase | Key Focus | Summary |
| 1 | Core Design & Serialization | Staff & Roster management, exceptions, JUnit tests |
| 2 | GUI Prototype | JavaFX bed/ward layout, resident assignment/move |
| 3 | Authorization & Prescriptions | Role-based access (Manager/Doctor/Nurse), logging, file persistence |
| 4 | Refactoring & Documentation | SOLID, patterns, reflection report & README update |

##  Persistence Files
| File | Purpose |
| `carehome.dat` | Serialized resident/staff/ward data |
| `prescriptions.dat` | Serialized prescription records |
| `carehome.log` | Plain-text system log of all actions |

## Design Patterns Used
Singleton – `LogService`
Strategy – `Repository<T, ID>` with different storage implementations
Facade – `AuthService` simplifies role & roster validation
Observer (implicit) – UI refresh upon data update

## Testing
JUnit 5 tests cover:
- Roster compliance (no >8 hrs/day)  
- Serialization (load/save)  
- Prescription service (add/list)
All tests passed

## Reflection
See (Reflection_Report) for detailed discussion.
