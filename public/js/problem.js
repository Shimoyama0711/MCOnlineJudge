$(function () {
    const path = location.pathname;

    console.log(`URL: ${path}`);

    // MDファイルを読み込む //
    fetch(`${path}.md`)
        .then(response => response.text())
        .then(fileContent => {
            console.log(fileContent);

            // GitHub Markdown API で変換 //
            fetch("https://api.github.com/markdown/raw", {
                method: "POST",
                headers: {
                    "Content-Type": "text/plain"
                },
                body: fileContent
            })
                .then(response => response.text())
                .then(response => {
                    const main = $("#main");
                    main.html(response);

                    const h1 = $("h1:first");
                    $("title").text(h1.text());

                    renderMath();
                });
        }
    );
});

function renderMath() {
    MathJax.typesetPromise()
        .then(() => {
            // レンダリング完了後の処理をここに記述する（任意）
        })
        .catch((err) => {
            console.error(err);
        });
}