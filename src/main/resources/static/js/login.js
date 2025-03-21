document.addEventListener("DOMContentLoaded", function () {
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const loginButton = document.getElementById("login-btn");
    const signupButton = document.getElementById("signup-btn");

    const emailError = document.getElementById("email-error");
    const passwordError = document.getElementById("password-error");

    function validateEmail(email) {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailPattern.test(email);
    }

    function validatePassword(password) {
        return password.length >= 8;
    }

    function validateInputs() {
        const email = emailInput.value.trim();
        const password = passwordInput.value.trim();
        let isValid = true;

        if (!validateEmail(email)) {
            emailError.textContent = "올바른 이메일 주소를 입력하세요.";
            isValid = false;
        } else {
            emailError.textContent = "";
        }

        if (!validatePassword(password)) {
            passwordError.textContent = "비밀번호는 8자 이상이어야 합니다.";
            isValid = false;
        } else {
            passwordError.textContent = "";
        }

        loginButton.disabled = !isValid;
        loginButton.style.backgroundColor = isValid ? "#7F6AEE" : "#ACA0EB";
    }

    emailInput.addEventListener("input", validateInputs);
    passwordInput.addEventListener("input", validateInputs);

    document.getElementById("login-form").addEventListener("submit", async function (event) {
        event.preventDefault();
        if (loginButton.disabled) return;

        const email = emailInput.value.trim();
        const password = passwordInput.value.trim();

        try {
            const response = await fetch("/users/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password })
            });

            const data = await response.json();
            if (response.ok) {
                alert("로그인 성공!");

                // JWT 토큰 저장 (API 응답에 token만 포함됨)
                if (data.token) {
                    localStorage.setItem("token", data.token); // JWT를 로컬 스토리지에 저장
                }

                window.location.href = "/posts";
            } else {
                alert(data.message || "로그인 실패");
            }
        } catch (error) {
            alert("로그인 중 오류가 발생했습니다.");
            console.error("Error:", error);
        }
    });

    signupButton.addEventListener("click", function () {
        window.location.href = "/users";
    });
});
