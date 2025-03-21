document.addEventListener("DOMContentLoaded", function () {
    // ✅ 로그인 여부 확인
    const userId = localStorage.getItem("user_id");
    const token = localStorage.getItem("token");
    if (!token) {
        alert("로그인이 필요합니다1.");
        window.location.href = "/users/login";
        return;
    }

    const titleInput = document.getElementById("title");
    const contentInput = document.getElementById("content");
    const imageInput = document.getElementById("image");
    const submitBtn = document.getElementById("submit-btn");

    // 제목과 내용이 입력되면 버튼 활성화
    function checkForm() {
        if (titleInput.value.trim() !== "" && contentInput.value.trim() !== "") {
            submitBtn.disabled = false;
            submitBtn.style.backgroundColor = "#7F6AEE";
        } else {
            submitBtn.disabled = true;
            submitBtn.style.backgroundColor = "#ACA0EB";
        }
    }

    titleInput.addEventListener("input", checkForm);
    contentInput.addEventListener("input", checkForm);

    // 이미지 파일을 Base64로 변환하는 함수
    function convertToBase64(file) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onloadend = () => resolve(reader.result);
            reader.onerror = reject;
            reader.readAsDataURL(file);
        });
    }

    // 게시글 제출
    submitBtn.addEventListener("click", async (event) => {
        event.preventDefault(); // 기본 제출 방지

        const title = titleInput.value.trim();
        const content = contentInput.value.trim();
        const imageFile = imageInput.files[0]; // 이미지 파일 가져오기
        let imageUrl = null;

        if (!title || !content) {
            alert("제목과 내용을 입력해주세요.");
            return;
        }

        // 이미지 파일이 선택된 경우 Base64로 변환
        if (imageFile) {
            try {
                imageUrl = await convertToBase64(imageFile);
            } catch (error) {
                console.error("이미지 변환 중 오류 발생:", error);
                alert("이미지 변환 중 오류가 발생했습니다.");
                return;
            }
        }

        const newPost = {
            userId: userId,
            title: title,
            content: content,
            imageUrl: imageUrl, // Base64 인코딩된 이미지 (없으면 null)
        };

        console.log("📌 전송할 데이터:", newPost);

        try {
            const response = await fetch("/posts", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(newPost),
            });

            if (!response.ok) throw new Error("게시글 작성 실패");

            alert("게시글이 등록되었습니다.");
            window.location.href = "/posts"; // 게시글 목록 페이지로 이동
        } catch (error) {
            console.error(error);
            alert("게시글 작성 중 오류가 발생했습니다.");
        }
    });
});
