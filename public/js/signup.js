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

    signupButton.on("click", async function () {
        const email = emailInput.val();
        const mcid = mcidInput.val();
        const password = passwordInput.val();
        const encrypted = await sha256(password);

        const alert = $("#emailAlert");

        // UUID取得 //
        $.ajax({
            url: `/get-uuid/${mcid}`,
            type: "GET",
            dataType: "text"
        }).done(function (data) {
            // console.log(data);

            const tmp = JSON.parse(data);
            const uuid = tmp["id"];
            const json = {email: email, mcid: mcid, encrypted: encrypted, uuid: uuid};

            $.ajax({
                url: "/signup",
                type: "POST",
                dataType: "text",
                data: JSON.stringify(json)
            }).done(function (data) {
                const json = JSON.parse(data);

                document.cookie = `uuid=${json["uuid"]}`;
                window.location.href = "./index.html";
            }).fail(function () {
                // console.log("[Ajax Failed]");
                // console.log(a);
                // console.log(b);
                // console.log(c);

                alert.css("display", "");
                alert.html(`
                <i class="bi-exclamation-circle-fill"></i>
                このEメールアドレスは既に登録されています
            `);
            });
        }).fail(function () {
            alert.css("display", "");
            alert.html(`
                <i class="bi-exclamation-circle-fill"></i>
                このMCIDは存在しません
            `);
        });
    });
});

async function sha256(data) {
    const encoder = new TextEncoder();
    const dataBuffer = encoder.encode(data);

    const hashBuffer = await crypto.subtle.digest('SHA-256', dataBuffer);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(byte => byte.toString(16).padStart(2, '0')).join('');
}

function buttonCheck(email, mcid, password, password2) {
    const regex = new RegExp("^[a-zA-Z0-9_+-]+(.[a-zA-Z0-9_+-]+)*@([a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9]*\\.)+[a-zA-Z]{2,}$");
    const b1 = regex.test(email);
    const b2 = mcid.length >= 3;
    const b3 = password.length >= 6;
    const b4 = password === password2;

    return (b1 && b2 && b3 && b4);
}