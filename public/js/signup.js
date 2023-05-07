$(function () {
    const emailInput = $("#emailInput");
    const mcidInput = $("#mcidInput");
    const passwordInput = $("#passwordInput");
    const passwordInput2 = $("#passwordInput2");
    const signupButton = $("#signupButton");

    emailInput.on("input", function () {
        if (buttonCheck(emailInput.val(), mcidInput.val(), passwordInput.val(), passwordInput2.val())) {
            signupButton.removeClass("disabled");
        } else {
            signupButton.addClass("disabled");
        }
    });

    mcidInput.on("input", function () {
        if (buttonCheck(emailInput.val(), mcidInput.val(), passwordInput.val(), passwordInput2.val())) {
            signupButton.removeClass("disabled");
        } else {
            signupButton.addClass("disabled");
        }
    });

    passwordInput.on("input", function () {
        if (buttonCheck(emailInput.val(), mcidInput.val(), passwordInput.val(), passwordInput2.val())) {
            signupButton.removeClass("disabled");
        } else {
            signupButton.addClass("disabled");
        }
    });

    passwordInput2.on("input", function () {
        if (buttonCheck(emailInput.val(), mcidInput.val(), passwordInput.val(), passwordInput2.val())) {
            signupButton.removeClass("disabled");
        } else {
            signupButton.addClass("disabled");
        }
    });

    signupButton.on("click", function () {
        const email = emailInput.val();
        const mcid = mcidInput.val();
        const password = passwordInput.val();
        const json = {email: email, mcid: mcid, password: password};

        $.ajax({
            url: "/signup",
            type: "POST",
            dataType: "text",
            data: JSON.stringify(json)
        }).done(function (data) {
            const json = JSON.parse(data);

            document.cookie = `uuid=${json["uuid"]}`;
            window.location.href = "./index.html";
        }).fail(function (a, b, c) {
            console.log("[Ajax Failed]");
            console.log(a);
            console.log(b);
            console.log(c);
            $("#emailAlert").css("display", "");
        });
    });
});

function buttonCheck(email, mcid, password, password2) {
    const regex = new RegExp("^[a-zA-Z0-9_+-]+(.[a-zA-Z0-9_+-]+)*@([a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9]*\\.)+[a-zA-Z]{2,}$");
    const b1 = regex.test(email);
    const b2 = mcid.length >= 3;
    const b3 = password.length >= 6;
    const b4 = password === password2;

    return (b1 && b2 && b3 && b4);
}