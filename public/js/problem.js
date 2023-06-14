$(function () {
    const h1 = $("h1:first");
    $("title").text(h1.text());

    MathJax.typesetPromise()
        .then(() => {
            // レンダリング完了後の処理をここに記述する（任意）
        })
        .catch((err) => {
            console.error(err);
        });
});