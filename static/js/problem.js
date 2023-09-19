$(function () {
    const problem = location.pathname.replace("/problem/", "");

    // CodeMirror 設定部分 //
    const editor = CodeMirror.fromTextArea(document.getElementById("editor"), {
        mode: "text/x-java",
        lineNumbers: true,
        theme: "default",
        tabSize: 4
    });

    // ボタンが押されたときの処理 //
    const submitButton = $("#submit-button");

    submitButton.on("click", function () {
        const array = {
            problem: location.pathname.replace("/problem/", "").replace(".html", ""),
            date: getFormattedDate(new Date()),
            body: editor.getValue()
        };

        console.log(JSON.stringify(array));

        $.ajax({
            url: "/send-judge",
            type: "POST",
            dataType: "json",
            data: JSON.stringify(array),
            headers: {
                "Content-Type": "application/json"
            }
        }).done(function() {
            // console.log("Ajax Successfully!");
            submitButton.addClass("disabled");
            submitButton.removeClass("btn-success");
            submitButton.addClass("btn-info");
            submitButton.html(`
                <i class="bi-hourglass-split"></i>
                送信中
            `);
            setTimeout(function() {
                location.href = "/submissions";
            }, 1000);
        }).fail(function(a, b, c) {
            console.log(a);
            console.log(b);
            console.log(c);
        });
    });

    // MathJax レンダリング //
    MathJax.typesetPromise();
});

function getFormattedDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');
    const second = String(date.getSeconds()).padStart(2, '0');

    return `${year}/${month}/${day} ${hour}:${minute}:${second}`;
}