$(function () {
    const emailInput = $("#emailInput");
    const passwordInput = $("#passwordInput");
    const loginButton = $("#loginButton");

    emailInput.on("input", function () {
        if (buttonCheck(emailInput.val(), passwordInput.val())) {
            loginButton.removeClass("disabled");
        } else {
            loginButton.addClass("disabled");
        }
    });

    passwordInput.on("input", function () {
        if (buttonCheck(emailInput.val(), passwordInput.val())) {
            loginButton.removeClass("disabled");
        } else {
            loginButton.addClass("disabled");
        }
    });
});

function buttonCheck(email, password) {
    const regex = new RegExp("^[a-zA-Z0-9_+-]+(.[a-zA-Z0-9_+-]+)*@([a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9]*\\.)+[a-zA-Z]{2,}$");
    const b1 = regex.test(email);
    const b2 = password.length >= 6;

    return (b1 && b2);
}