document.addEventListener("DOMContentLoaded", async function () {
    // 헤더 동적 로드
    const response = await fetch("/header");
    const headerHtml = await response.text();
    document.body.insertAdjacentHTML("afterbegin", headerHtml);

    setupDropdown();
    setupBackButton();
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
                window.location.href = `/users/${userId}/profile`;
            } else if (event.target.id === "edit-password") {
                window.location.href = `/users/${userId}/password`;
            } else if (event.target.classList.contains("logout")) {
                localStorage.removeItem("user_id"); // 로그아웃 시 user_id 삭제
                window.location.href = "/users/login";
            }
        });
    });

}
function setupBackButton() {
    const backBtn = document.querySelector("#back-btn");

    if (backBtn) {
        backBtn.addEventListener("click", (event) => {
            event.preventDefault();
            window.location.href = "/posts"; // 뒤로가기 버튼 클릭 시 경로 이동
        });
    }
}

