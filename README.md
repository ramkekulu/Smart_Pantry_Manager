Smart Pantry Manager
App Description
Smart Pantry Manager is an Android mobile application developed to help users manage food ingredients that they already have at home and find recipes that can be prepared using those ingredients.
The application allows users to:
•	Add pantry ingredients
•	View pantry ingredients
•	Edit ingredients
•	Delete ingredients
•	Store ingredient quantities and units
•	View suggested recipes
•	View recipe details
•	Manage profile/settings
The application uses strict recipe matching. A recipe is only displayed when all of its required ingredients are available in the user's pantry. This helps users make better use of existing ingredients and supports the reduction of unnecessary food waste.
Technologies Used
•	Java
•	Android Studio
•	XML
•	SQLite
•	RecyclerView
•	Git and GitHub
Database Option
SQLite
The application uses SQLite as its local database.
SQLite was chosen because it is lightweight, suitable for Android applications and does not require a separate database server. It allows the application to store structured information locally on the user's device.
SQLite is used to store:
•	Pantry ingredients
•	Ingredient quantities
•	Ingredient units
•	Recipes
•	Recipe ingredients
•	Recipe information
The database also supports the application's CRUD operations:
•	Create – add pantry ingredients
•	Read – view pantry ingredients
•	Update – edit pantry ingredients
•	Delete – remove pantry ingredients
Using SQLite allows pantry information to remain available when the application is closed and opened again.
Database Tables
The main database tables are:
PANTRY_ITEM
id
name
quantity
unit
RECIPE
id
name
description
instructions
RECIPE_INGREDIENT
id
recipe_id
ingredient_name
quantity
unit
The application compares pantry ingredients with recipe ingredients to determine which recipes can be prepared.
Setup and Run Instructions
Requirements
Before running the application, install:
•	Android Studio
•	Java Development Kit (JDK)
•	Android SDK
•	Android emulator or physical Android device
Step 1: Clone the Repository
Open Android Studio and select:
File → New → Project from Version Control
Select Git and enter the GitHub repository URL.
Step 2: Open the Project
Open the cloned Smart Pantry Manager project in Android Studio.
Allow Android Studio to complete Gradle synchronisation and download any required dependencies.
Step 3: Select a Device
Connect an Android phone with USB debugging enabled or start an Android emulator using Android Studio's Device Manager.
Step 4: Run the Application
In Android Studio:
1.	Select the Smart Pantry Manager application.
2.	Select the connected device or emulator.
3.	Click the green Run button.
4.	Wait for Android Studio to build and install the application.
5.	The application will open on the selected device.
Testing the Application
Pantry CRUD Testing
To test the main pantry functionality:
1.	Add an ingredient.
2.	Confirm that it appears in the pantry.
3.	Edit the ingredient.
4.	Confirm that the changes are saved.
5.	Delete the ingredient.
6.	Confirm that it is removed.
Strict Recipe Matching Testing
To test strict recipe matching:
1.	Add all ingredients required by a recipe.
2.	Open Suggested Recipes.
3.	Confirm that the recipe appears.
4.	Remove one required ingredient.
5.	Confirm that the recipe disappears.
6.	Add the missing ingredient again.
7.	Confirm that the recipe appears again.
Database Persistence Testing
To test database persistence:
1.	Add pantry ingredients.
2.	Close the application.
3.	Open the application again.
4.	Confirm that the ingredients are still available.
GitHub Repository
Repository:
https://github.com/ramkekulu/Smart_Pantry_Manager.git
Author
Student Name: Ramadimetja Esther Makhetha
Student Number: 401400378
Course: Mobile Application Development 700
Year: 2026
