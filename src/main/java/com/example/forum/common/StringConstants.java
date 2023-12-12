package com.example.forum.common;

public class StringConstants {
    public static final String[] SKIP_URLS = {"/css/**", "/webfonts/**",
            "/img/**", "/js/**", "/login", "/favicon.ico", "/error/**", "/error" , "/registry"};

    public static final String MAIL_BODY_USER_SUCCESSFUL_REGISTRATION =
            """
                    Hello,
                    You have successfully requested registration on our closed forum.
                    Now, the matter remains in the hands of our administrator;
                    When he considers your application, we will notify you.
                    Ignore this email if you have not made the request remember your password.""";
    public static final String MAIL_SUBJECT_USER_SUCCESSFUL_REGISTRATION = "Successfully registration request";
    public static final String MAIL_BODY_ADMIN_REGISTERED =
            """
                    Hello,
                    We registered you on our private forum.
                    Your email:
                    %s
                    Your username:
                    %s
                    Your password:
                    %s
                    Our link:
                    %s
                    You can use both your username and password as your login.""";
    public static final String MAIL_SUBJECT_ADMIN_REGISTERED = "you were registered on the forum";
    public static final String MAIL_BODY_ACCESS_BLOCKED =
            """
                    Hello!
                    You violated the rules of our community, for which you were temporarily blocked
                    You will receive a letter about unlocking again.
                    Our link:
                    %s""";
    public static final String MAIL_SUBJECT_ACCESS_BLOCKED = "YOU ARE BLOCKED";
    public static final String MAIL_BODY_ACCESS_UNBLOCKED =
            """
                    Hello!
                    Already you have access to our forum.
                    Our link:
                    %s
                    You can use both your username and password as your login.""";
    public static final String MAIL_SUBJECT_ACCESS_UNBLOCKED = "You have access...";
}
