# The Task
Create a multi-platform Cocktail recipe app which allows users to manage recipes and keeps those recipes stored in a local database. The UI should be Material 3, easy to use, engaging & delightful to use. It must also follow good Accessibility practices to be fully usable by screen a reader, those with low vision, and color blindness.

## Requirements
- Work on Android, iOS, & Desktop for Mac OS and Linux
- Be Accessible to screen readers
- Be an offline-1st app
- Use Material 3 UI/UX
- Allow light and dark mode
- Allow users to take pictures to add to recipes
- Allow rich text input for about, instructions, & notes
- Provide user account management
- Support portrait & landscape orientation

## Features
- Login
- Account Page
  - change password
  - delete account
  - log out
  - show user info
- List recipe View
  - Add a recipe
  - Delete a recipe
  - Edit a recipe
  - View a recipe
  - sort recipes (save sort state in a local preference based datastore)
    - by date created, date updated
    - by name
  - filter recipes (save filter state in a local preference based datastore)
    - by tags, ingredients, alcoholic
  - Switch between list or grid views
  - Search
    - by name, tags, and about
    - by ingredients
  - show and allow marking a recipe as a favorite
- Detail recipe View
  - Delete the recipe
  - Share a recipe (plain text)
  - Edit a recipe
  - favorite a recipe
- Create/Edit recipe view
  - Take a picture for the recipe image
  - markdown or rich text editor for appropriate fields
  - favorite a recipe
