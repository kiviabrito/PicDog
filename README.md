# PicDog Android App - Kotlin

## Functionality

Show dog pictures by the breed.

## Structure:
Some os the components used are:

- MVVM Architecture.
- Room DataBase - for local caching.
- Coroutines - for tasks on the background, as network calls and access to database.
- Retrofit - for the network calls.
- Gson - for the deserialization.
- Glide - for image loading and cache.
- RecyclerView - for displaying list of Pictures.
- ConstraintLayout - for the layout design.
- CardView - for the layout design.


## Models:
Two different models were receive from a success API calls :

- [ ] https://iddog-nrizncxqba-uc.a.run.app/feed - >
- FeedEntity - with the category(breed), and a list of url photos.

- [ ] https://iddog-nrizncxqba-uc.a.run.app/signUp - >
- UserResponse - with the user(UserEntity) information.

Retrofit + Gson are working together to deserialize the Json, response from the API call, to those 2 models described above.
In case of an error response, ErrorResponse was create to catch the message.

For the database it is using the "UserEntity" and "FeedEntity", which contain relevante information to run the app on the offline mode.

For the picture cashing, it is using Glide.

## Views:
For the view it is using three Activities (SplashActivity, AuthActivity and MainActivity).

- SplashActivity : 

    It displays the app logo, the only functionality is to redirect the user wherever he is signed up or not.

- AuthActivity : 

    It is displaying a welcome page.

    User Interaction : 

    EditText where the user should enter the email, and a "SIGN UP" Button where the user can sign up to the app.

![alt text](https://github.com/kiviabrito/PicDog/blob/master/Screenshot_AuthActivity.png) 

- MainActivity : 

    It has four tabs, for the four different breeds and a ViewPager, which displays the tab view with a fragment (MainFragment).
    It also includes a "SIGN OUT" button.

    User Interaction:

    Switch between tabs(displaying fotos from different breeds), when clicking on a picture it shows a dialog with the expanded picture, and the user also has a option to sign out.


![alt text](https://github.com/kiviabrito/PicDog/blob/master/Screenshot_MainActivity.png) 


## Executar o app

No update is necessary, you can just clone the project and run it.
#### Note: For the app work on the offline mode it is necessary that the user has had at least one access with the internet connected, so it can fetch the data from the network and store it on the local data base.
