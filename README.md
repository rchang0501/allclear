# allclear
Skincare management Android application using Jetpack for UI/UX development and Room for data handling. Employs modern Kotlin development principles and the MVVM architecture pattern.

<p align="center">
  <img src="/demo/rejuvenate_icon_circle-01.png" alt="rejuvenate_icon" width="150" height="150"/>
</p>

### Android Tech Stack
**Languages**: Kotlin </br>
**Architecture Pattern**: MVVM </br>
**Jetpack Libraries**: Room, Material Design Components, DataBinding, Lifecycle, Navigation, RecyclerView, AppCompat, Fragment </br>
**IDE**: Android Studio </br>

## Side-by-Side iOS and Android Demo

### Launch and Home Screen
Startup time between platforms is closely matched. All views also contain a rubber band effect to enhance UX. 

![Rejuvenate Launch](/demo/1_intro.gif)

### Toggle Skincare Reminders as Complete or Incomplete
Toggling the complete button animates the central custom progress view. When all items in the list are completed, the progress view fills up with a green colour. Animations are closely matched between platforms with a slight difference with how the Android ListAdapter handles animations when one of its items has been updated. 

![Rejuvenate Skincare Reminder Toggle](/demo/2_toggle_buttons.gif)

### Navigate through Skincare Reminder Time Filters
The top navbar can be used to filter the list of skincare reminders by the date they are to be completed. Animations between platforms upon filtering are closely matched. A custom segmented control nav bar was implemented for Android to match the UI of the iOS version.

![Rejuvenate Skincare Reminder Time Filters](/demo/3_scroll_through_other_times.gif)

### Add a new Skincare Reminder
The following illustrates the process to add a new skincare reminder to allclear

#### 1. Tap the `+` icon in the top right corner

![Rejuvenate Add Skincare Reminder Screen](/demo/4_add_reminder.gif)

#### 2. Add a new title 

![Rejuvenate Add Skincare Reminder Title](/demo/5_add_reminder_text.gif)

#### 3. Select a date and time 

![Rejuvenate Add Skincare Reminder Time](/demo/6_add_reminder_time.gif)

#### 4. Optionally add notes

![Rejuvenate Add Skincare Reminder Notes](/demo/7_add_reminder_notes.gif)

#### 5. Tap the `done` button in the top right corner 

![Rejuvenate Add Skincare Reminder Save](/demo/8_add_reminder_save.gif)

### Navigate to Skincare Reminder Detail View
Tap a skincare reminder list item to navigate to the detail view. The detail view shows the skincare reminder's Title, Date, Time, and Notes.  

![Rejuvenate Skincare Reminder Detail](/demo/9_detail_view.gif)

### Edit Skincare Reminder
Tap the `edit` button in the top right corner to edit the skincare reminder. The edit view reuses the "add new reminder" view. The difference between them is that the edit view auto-populates already filled-out fields. All fields can be changed, the demo below shows the date being changed only. 

![Rejuvenate Skincare Reminder Edit](/demo/10_edit_view.gif)

### Delete Skincare Reminder
Swiping a skincare reminder item to the left will delete it. Changes will persist upon navigating to a different view or closing the app. The central progress view also animates in response to the change. 

![Rejuvenate Skincare Reminder Delete](/demo/11_delete_reminder.gif)

### Dark Mode
allclear has a fully operational dark mode and light mode. The app will automatically detect when the system enters dark/light mode and updates all views accordingly. To be concise, only the home screen's dark mode is demonstrated. 

![Rejuvenate Dark Mode](/demo/12_dark_mode.gif)
