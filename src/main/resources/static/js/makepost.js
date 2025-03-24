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

    // 이미지 파일을 서버에 업로드하는 함수
    async function uploadImage(file) {
        const formData = new FormData();
        formData.append("image", file);

        try {
            const response = await fetch("/posts/upload/image", {
                method: "POST",
                body: formData
            });

            if (!response.ok) throw new Error("이미지 업로드 실패");

            const data = await response.json();
            return data.imageUrl; // 서버에서 반환된 이미지 URL을 리턴
        } catch (error) {
            console.error("이미지 업로드 중 오류 발생:", error);
            alert("이미지 업로드 중 오류가 발생했습니다.");
            return null;
        }
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

        // 이미지 파일이 선택된 경우 서버로 업로드
        if (imageFile) {
            imageUrl = await uploadImage(imageFile);
            console.log("이미지 url:",imageUrl);
            if (!imageUrl) return; // 이미지 업로드 실패 시 종료
        }

        const newPost = {
            userId: userId,
            title: title,
            content: content,
            imageUrl: imageUrl, // 서버에서 받은 이미지 URL
        };

        console.log("📌 전송할 데이터:", newPost);

        try {
            const response = await fetch("/posts", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`, // JWT 토큰을 Authorization 헤더로 보냄
                },
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
