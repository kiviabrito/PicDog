
# PicDog para Android - Kotlin

## Funcionalidade

Exibe fotos de cachorro pela raça.

## Estrutura:
Alguns dos componentes utilizados são:

- Arquitetura MVVM.
- Room DataBase - para armazenamento em cache local.
- Corotinas - para tarefas em segundo plano, como chamadas de rede e acesso ao banco de dados.
- Retrofit - para as chamadas de rede.
- Gson - pela desserialização.
- Glide - para carregamento e cache de imagens.
- RecyclerView - para exibir a lista de imagens.
- ConstraintLayout - para o design do layout.
- CardView - para o design do layout.

##Libs:

   **JUNIT JUPITER (JUnit5) - Para test Unitário**
   
                def junit_jupiter_version = "5.3.2"
                testImplementation "org.junit.jupiter:junit-jupiter-api:$junit_jupiter_version"
                testRuntimeOnly "org.junit.jupiter:junit-jupiter-engine:$junit_jupiter_version"
            
   **COROUTINES-TEST - Para test Unitário**
   
                testImplementation 'org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.6'
            
   **MOCKITO - Para test Unitário.**
   
                def mockito_version = "2.25.0"
                testImplementation "org.mockito:mockito-core:$mockito_version"
                testImplementation "com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0"
            
   **GSON - Para desserialização de error.**
   
                implementation 'com.google.code.gson:gson:2.8.6'
            
   **RETROFIT - Para chamadas de rede.**
   
                def retrofitVersion = "2.7.1"
                implementation ("com.squareup.retrofit2:retrofit:$retrofitVersion")
                implementation "com.squareup.retrofit2:converter-gson:$retrofitVersion"
            
   **ROOM DATABASE - Para armazenamento local de dados.**
   
                def room_version = "2.2.5"
                implementation "androidx.room:room-runtime:$room_version"
                kapt "androidx.room:room-compiler:$room_version"
                implementation "androidx.room:room-ktx:$room_version"
            
   **GLIDE - Para download e cache das imagens.**
   
                def glide_version = "4.11.0"
                implementation "com.github.bumptech.glide:glide:$glide_version"
                kapt "com.github.bumptech.glide:compiler:$glide_version"


## Modelos:
Dois modelos diferentes foram recebidos das chamadas de API bem-sucedida:

- [] https://iddog-nrizncxqba-uc.a.run.app/feed ->
- FeedEntity - com a categoria (raça) e uma lista com as url das fotos.

- [] https://iddog-nrizncxqba-uc.a.run.app/signUp ->
- UserResponse - com as informações do usuário (UserEntity).

O Retrofit + Gson estão trabalhando juntos para desserializar o Json, resposta da chamada da API, para os dois modelos descritos acima.
No caso de uma resposta de erro, o ErrorResponse foi criado para capturar a mensagem.

Para o banco de dados, está sendo usado "UserEntity" e "FeedEntity", que contêm informações relevantes para executar o aplicativo no modo offline.

Para a obtenção de imagens, está send usando o Glide.

## Visualizações:
Para a visualização, está sendo utilizada três atividades (SplashActivity, AuthActivity e MainActivity).

- SplashActivity:

    Exibe o logotipo do aplicativo, a única funcionalidade é redirecionar o usuário caso ele esteja logado ou não.

- AuthActivity:

    Está exibindo uma página de boas-vindas.

    Interação com o usuário :

    EditText onde o usuário deve inserir o email e um botão "SIGN UP", onde o usuário pode se inscrever no aplicativo.


![alt text](https://github.com/kiviabrito/PicDog/blob/master/Screenshot_AuthActivity.png) 


- MainActivity :

    Possui quatro guias, para as quatro raças diferentes e um ViewPager, que exibe a list de imagens no fragmento (MainFragment).
    Também inclui um botão "SIGN OUT".

    Interação com o usuário:

    Alterne entre as guias (exibindo fotos de diferentes raças). Ao clicar numa imagem, é exibida uma caixa de diálogo com a imagem expandida. O usuário também tem a opção de sair da conta.


![alt text](https://github.com/kiviabrito/PicDog/blob/master/Screenshot_MainActivity.png) 


## Executar o app

Nenhuma atualização é necessária, basta clonar o projeto e executá-lo.
Nota: Para o aplicativo funcionar no modo offline, é necessário que o usuário tenha pelo menos um acesso com a Internet conectada, para que possa buscar os dados da rede e armazená-los na base de dados local.