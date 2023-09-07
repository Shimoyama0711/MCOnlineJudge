$(function () {
    let fileInput = $("#audioLoader");

    fileInput.on("change", function () {
        let filename = $(this).val().split('\\').pop();

        const audio = new Audio(filename);
        audio.play().then(r => {});

        console.log(`Filepath: ${filename}`);

        let file = this.files[0];
        let reader = new FileReader();
        reader.readAsText(file);

        reader.onload = function (event) {
            let contents = event.target.result;
            console.log(contents);
        };

        setTimeout(toBlack, 400);
    });

    function toBlack() {
        const smile1 = $("#smile1");
        const smile2 = $("#smile2");
        const smile3 = $("#smile3");

        smile1.removeClass("bi-emoji-smile");
        smile2.removeClass("bi-emoji-smile");
        smile3.removeClass("bi-emoji-smile");
        smile1.addClass("bi-emoji-smile-fill");
        smile2.addClass("bi-emoji-smile-fill");
        smile3.addClass("bi-emoji-smile-fill");

        setTimeout(toWhite, 400);
    }

    function toWhite() {
        const smile1 = $("#smile1");
        const smile2 = $("#smile2");
        const smile3 = $("#smile3");

        smile1.removeClass("bi-emoji-smile-fill");
        smile2.removeClass("bi-emoji-smile-fill");
        smile3.removeClass("bi-emoji-smile-fill");
        smile1.addClass("bi-emoji-smile");
        smile2.addClass("bi-emoji-smile");
        smile3.addClass("bi-emoji-smile");

        setTimeout(toBlack, 400);
    }
});