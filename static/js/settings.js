$(function () {
    const logoutButton = $("#logoutButton");

    logoutButton.on("click", signOut);
});

function signOut() {
    document.cookie = "uuid=;";
    window.location.href = "/";
}