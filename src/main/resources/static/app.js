const API_BASE_URL = "http://localhost:8080"; // 서버 주소
let jwtToken = null; // JWT 토큰 저장
let stompClient = null; // WebSocket 클라이언트

// 로그인 버튼 클릭 이벤트
document.getElementById('loginButton').addEventListener('click', async () => {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    if (!username || !password) {
        alert("Please enter username and password!");
        return;
    }

    // 로그인 API 호출
    try {
        const response = await fetch(`${API_BASE_URL}/members/sign-in`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const data = await response.json();
            jwtToken = data.accessToken; // JWT 토큰 저장
            console.log("Login successful! Token:", jwtToken);

            // WebSocket 연결 및 구독 시작
            connectToWebSocket(jwtToken);
        } else {
            alert("Login failed!");
            console.error("Login error:", await response.text());
        }
    } catch (error) {
        console.error("Login error:", error);
    }
});

// WebSocket 연결 및 구독
function connectToWebSocket(token) {
    const socket = new SockJS(`${API_BASE_URL}/websocket`);
    stompClient = Stomp.over(socket);

    stompClient.connect({ Authorization: `Bearer ${token}` }, (frame) => {
        console.log('Connected to WebSocket:', frame);

        // JWT 디코딩 후 idx 추출
        const idx = parseJwt(token).idx;
        console.log("Extracted idx:", idx);

        // WebSocket 경로 구독
        const subscriptionPath = `/socket/${idx}`;
        stompClient.subscribe(subscriptionPath, (message) => {
            console.log("Message received:", message.body);
            displayMessage(message.body); // 메시지 화면에 표시
        });

        alert(`Subscribed to WebSocket path: ${subscriptionPath}`);
    }, (error) => {
        console.error("WebSocket connection error:", error);
    });
}

// JWT 디코딩 함수
function parseJwt(token) {
    const base64Url = token.split('.')[1];
    const base6
