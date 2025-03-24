document.addEventListener("DOMContentLoaded", async function () {
    const passwordInput = document.getElementById("password");
    const passwordConfirmInput = document.getElementById("password-confirm");
    const submitBtn = document.getElementById("submit-btn");

    // hidden input에서 userId 가져오기
    const userId = document.getElementById("user-id")?.value;
    const token = localStorage.getItem("token");

    if (!userId || !token) {
        alert("잘못된 접근입니다.");
        window.location.href = "/users/login";
        return;
    }

    function validatePasswords() {
        const password = passwordInput.value;
        const confirmPassword = passwordConfirmInput.value;
        const passwordError = document.getElementById("password-error");
        const confirmPasswordError = document.getElementById("password-confirm-error");

        passwordError.textContent = "";
        confirmPasswordError.textContent = "";

        let isValid = true;

        // 비밀번호 검증 (8~20자, 대소문자, 숫자, 특수문자 포함)
        if (password.length < 8 || password.length > 20 ||
            !/[A-Z]/.test(password) || !/[a-z]/.test(password) ||
            !/[0-9]/.test(password) || !/[\W_]/.test(password)) {
            passwordError.textContent = "비밀번호는 8~20자이며, 대문자/소문자/숫자/특수문자를 포함해야 합니다.";
            isValid = false;
        }

        // 비밀번호 확인 일치 여부
        if (confirmPassword !== password) {
            confirmPasswordError.textContent = "비밀번호가 일치하지 않습니다.";
            isValid = false;
        }

        submitBtn.disabled = !isValid;
    }

    passwordInput.addEventListener("input", validatePasswords);
    passwordConfirmInput.addEventListener("input", validatePasswords);

    // 비밀번호 변경 API 호출
    document.getElementById("password-form").addEventListener("submit", async function (e) {
        e.preventDefault();

        const newPassword = passwordInput.value.trim();

        try {
            const response = await fetch(`/users/${userId}/password`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({ password: newPassword }),
            });

            if (!response.ok) throw new Error("비밀번호 변경 실패");

            alert("비밀번호가 성공적으로 변경되었습니다.");
            window.location.href = "/users/login";
        } catch (error) {
            console.error(error);
            alert("비밀번호 변경에 실패했습니다.");
        }
    });
});