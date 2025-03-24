document.addEventListener("DOMContentLoaded", async () => {
    const updateBtn = document.querySelector(".update-btn");
    const deleteBtn = document.querySelector(".delete-btn");
    const modal = document.querySelector(".modal");
    const closeBtn = document.querySelector(".close-btn");
    const nicknameInput = document.querySelector("#nickname");
    const helperText = document.querySelector(".helper-text");

    const changeBtn = document.querySelector("#change-btn");
    const profilePicInput = document.querySelector("#profile-pic-input");
    const profilePic = document.querySelector("#profile-pic");

    const userId = document.getElementById("user-id")?.value;
    const token = localStorage.getItem("token");

    if (!userId || !token) {
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

    // 프로필 사진 변경 버튼 클릭 시 파일 입력 필드 활성화
    changeBtn.addEventListener("click", () => {
        profilePicInput.click(); // 파일 선택 input 클릭
    });

    // 파일이 선택되었을 때 프로필 사진 미리보기
    profilePicInput.addEventListener("change", (event) => {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                profilePic.src = e.target.result; // 미리보기 이미지 변경
            };
            reader.readAsDataURL(file); // 파일을 읽어서 미리보기 표시
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
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({ nickname: newNickname }),
            });

            if (!response.ok) throw new Error("닉네임 변경 실패");

            alert("닉네임이 성공적으로 변경되었습니다.");
            modal.classList.add("hidden");
        } catch (error) {
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
            const response = await fetch(`/users/${userId}`, {
                method: "DELETE",
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            });

            if (!response.ok) throw new Error("회원 탈퇴 실패");

            alert("회원 탈퇴가 완료되었습니다.");
            localStorage.removeItem("token"); // 토큰 삭제
            localStorage.removeItem("user_id"); // 유저 ID 삭제
            window.location.href = "/users/login";
        } catch (error) {
            console.error(error);
            alert("회원 탈퇴에 실패했습니다.");
        }
    });
});
