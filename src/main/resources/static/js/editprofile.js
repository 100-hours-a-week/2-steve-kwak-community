document.addEventListener("DOMContentLoaded", async () => {
    const updateBtn = document.querySelector(".update-btn");
    const deleteBtn = document.querySelector(".delete-btn");
    const modal = document.querySelector(".modal");
    const closeBtn = document.querySelector(".close-btn");
    const nicknameInput = document.querySelector("#nickname");
    const helperText = document.querySelector(".helper-text");

    const userId = document.getElementById("user-id")?.value;

    if (!userId) {
        alert("잘못된 접근입니다.");
        window.location.href = "/users/login";
        return;
    }

    // 닉네임 입력 검증
    nicknameInput.addEventListener("input", () => {
        if (nicknameInput.value.length > 10) {
            helperText.textContent = "*닉네임은 최대 10자까지 가능합니다.";
            helperText.style.color = "red";
        } else {
            helperText.textContent = "";
        }
    });

    // 수정하기 버튼 클릭 시 API 호출
    updateBtn.addEventListener("click", async () => {
        const newNickname = nicknameInput.value.trim();

        if (newNickname === "") {
            helperText.textContent = "*닉네임을 입력해주세요.";
            helperText.style.color = "red";
            return;
        }

        try {
            const response = await fetch(`/users/${userId}`, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ nickname: newNickname }),
            });

            if (!response.ok) throw new Error("닉네임 변경 실패");

            alert("닉네임이 성공적으로 변경되었습니다.");
            modal.classList.add("hidden");
        } catch (error) {
            console.error(error);
            alert("닉네임 변경에 실패했습니다.");
        }
    });

    // 모달 닫기 버튼
    closeBtn.addEventListener("click", () => {
        modal.classList.add("hidden");
    });

    // 회원 탈퇴 API 호출
    deleteBtn.addEventListener("click", async () => {
        const confirmDelete = confirm("정말로 회원 탈퇴를 진행하시겠습니까?");
        if (!confirmDelete) return;

        try {
            const response = await fetch(`/users/${userId}`, { method: "DELETE" });
            if (!response.ok) throw new Error("회원 탈퇴 실패");

            alert("회원 탈퇴가 완료되었습니다.");
            localStorage.removeItem("user_id"); // 로컬 스토리지에서 유저 ID 삭제
            window.location.href = "/users/login";
        } catch (error) {
            console.error(error);
            alert("회원 탈퇴에 실패했습니다.");
        }
    });
});
