document.addEventListener("DOMContentLoaded", async function () {
    try {
        // 헤더 동적 로드
        const response = await fetch("/header1");
        if (!response.ok) {
            throw new Error("헤더 로드 실패: " + response.statusText);
        }
        const headerHtml = await response.text();
        document.body.insertAdjacentHTML("afterbegin", headerHtml);

        setupDropdown();
        setupBackButton();
    } catch (error) {
        console.error("헤더 로드 중 오류 발생:", error);
    }
});

function setupDropdown() {
    const profileDropdown = document.querySelector(".profile-dropdown");
    const dropdownMenu = document.querySelector(".dropdown-menu");
    const dropdownItems = document.querySelectorAll(".dropdown-item");

    if (!profileDropdown || !dropdownMenu || dropdownItems.length === 0) {
        console.warn("드롭다운 요소를 찾을 수 없습니다.");
        return;
    }

    console.log("드롭다운 아이템들:", dropdownItems);  // dropdownItems가 제대로 선택되었는지 확인

    // 현재 로그인된 사용자 ID 가져오기
    const userId = localStorage.getItem("user_id");
    console.log("로그인된 사용자 ID:", userId);  // 로그인된 사용자 ID 확인

    if (!userId) {
        console.warn("로그인된 사용자 ID를 찾을 수 없습니다.");
        return;
    }

    // 프로필 클릭 시 드롭다운 표시
    profileDropdown.addEventListener("click", (event) => {
        event.preventDefault(); // 기본 이벤트 방지
        dropdownMenu.classList.toggle("show"); // class를 이용한 토글
    });

    // 드롭다운 메뉴 클릭 이벤트 추가 (user_id 포함)
    dropdownItems.forEach((item) => {
        item.addEventListener("click", (event) => {
            event.preventDefault(); // 기본 이벤트 방지

            const targetId = event.target.id;
            console.log("클릭된 항목 ID:", targetId);  // 클릭된 항목의 ID 확인

            if (targetId === "edit-profile") {
                window.location.href = `/users/${userId}/profile`;
            } else if (targetId === "edit-password") {
                const passwordUrl = `/users/${userId}/password`;
                console.log("비밀번호 경로:", passwordUrl); // 경로 확인
                window.location.href = passwordUrl;
            } else if (event.target.classList.contains("logout")) {
                localStorage.removeItem("user_id"); // 로그아웃 시 user_id 삭제
                window.location.href = "/users/login";
            }
        });
    });

    // 페이지 클릭 시 드롭다운 닫기
    document.addEventListener("click", (event) => {
        if (!profileDropdown.contains(event.target) && !dropdownMenu.contains(event.target)) {
            dropdownMenu.classList.remove("show");
        }
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
