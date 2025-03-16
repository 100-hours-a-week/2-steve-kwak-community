document.addEventListener("DOMContentLoaded", async function () {
    // 헤더 동적 로드
    const response = await fetch("header.html");
    const headerHtml = await response.text();
    document.body.insertAdjacentHTML("afterbegin", headerHtml);

    setupDropdown();
});

function setupDropdown() {
    const profileDropdown = document.querySelector(".profile-dropdown");
    const dropdownMenu = document.querySelector(".dropdown-menu");
    const dropdownItems = document.querySelectorAll(".dropdown-item");

    // 현재 로그인된 사용자 ID 가져오기 추후에 가져오는 방식 수정해야함.
    const userId = localStorage.getItem("user_id");

    // 프로필 클릭 시 드롭다운 표시
    profileDropdown.addEventListener("click", () => {
        dropdownMenu.style.display = dropdownMenu.style.display === "block" ? "none" : "block";
    });

    // 드롭다운 메뉴 클릭 이벤트 추가 (user_id 포함)
    dropdownItems.forEach((item) => {
        item.addEventListener("click", (event) => {
            if (event.target.id === "edit-profile") {
                window.location.href = `../editprofile/editprofile.html?user_id=${userId}`;
            } else if (event.target.id === "edit-password") {
                window.location.href = `../editpassword/editpassword.html?user_id=${userId}`;
            } else if (event.target.classList.contains("logout")) {
                localStorage.removeItem("user_id"); // 로그아웃 시 user_id 삭제
                window.location.href = "../login/login.html";
            }
        });
    });
}
