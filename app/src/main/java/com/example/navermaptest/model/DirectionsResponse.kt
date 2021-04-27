package com.example.navermaptest.model

import com.naver.maps.geometry.LatLng

/** 하위 구조체는 Object로 표현하며, route 내 각 항목의 속성은 일반적인 key-value 쌍("key": "value")으로 표현합니다. */
data class DirectionsResponse(
    val code: Int,                          // 응답 결과 코드
    val message: String,                    // 응답 결과 문자열
    val currentDateTime: String,            // 탐색 시점 시간 정보
    val route: Map<String, List<Route>>     // 응답 결과
) {
    val firstRoute
        get() = route.asIterable().firstOrNull()?.value?.firstOrNull()
}

/** 경로 안내를 위한 속성들의 대분류를 나타냅니다. */
data class Route(
    val summary: Summary,           // 요약 정보
    val path: List<List<Double>>,   // 경로를 구성하는 모든 좌표열.해당 좌표들에는 0번부터 시작되는 index 가 있으며, 이 index 는 경로 정보를 표현하기 위한 pointIndex 라는 명칭으로 활용됩니다.
    val section: List<Section>?,          // 해당 경로를 구성하는 주요 도로에 관한 정보열(모든 경로를 포함하는 정보는 아닙니다.)
    val guide: List<Guide>?               // 안내 정보열
) {
    val coords
        get() = path.map { LatLng(it[1], it[0]) }
}

/** 탐색된 경로의 요약 정보를 나타냅니다. */
data class Summary(
    val start: ResponsePositionFormat,              // 출발지
    val goal: ResponsePositionFormat,               // 목적지
    val waypoints: List<ResponsePositionFormat>?,   // 경유지. 경유하는 순서대로 array 에 기록
    val distance: Int,                              // 전체 경로 거리 (meters)
    val duration: Int,                              // 전체 경로 소요 시간 (milliseconds)
    val bbox: List<List<Double>>,                   // 전체 경로 경계 영역. 두개의 point array 로 제공
    val tollFare: Int,                              // 통행 요금 (톨게이트)
    val taxiFare: Int,                              // 택시 요금 (지자체별, 심야, 시경계, 복합, 콜비 감안)
    val fuelPrice: Int                              // 해당 시점의 전국 평균 유류비와 연비를 감안한 유류비
)

/** 탐색된 경로 중 주요 도로의 정보를 나타냅니다. 도로명을 기준으로 주행 길이가 긴 경로의 정보입니다. */
data class Section(
    val pointIndex: Int,        // 경로를 구성하는 좌표의 인덱스
    val pointCount: Int,        // 형상점 수
    val distance: Int,          // 거리 (meters)
    val name: String,           // 도로명
    val congestion: Int?,       // 구간 혼잡도 (1: 원활, 2: 서행, 3: 혼잡)
    val speed: Int?             // 평균 속도 (km/h)
)

/** 회전 안내가 필요한 지점과 회전 안내까지의 거리 정보를 제공합니다. */
data class Guide(
    val pointIndex: Int,        // 경로를 구성하는 좌표의 인덱스
    val type: Int,              // 안내 종류
    val instructions: String?,  // 안내 문구
    val distance: Int,          // 이전 guide unit 의 경로 구성 좌표 인덱스로부터 해당 guide unit 의 경로 구성 좌표 인덱스까지의 거리 (meters)
    val duration: Int           // 이전 guide unit 의 경로 구성 좌표 인덱스로부터 해당 guide unit 의 경로 구성 좌표 인덱스까지의 소요 시간 (milliseconds)
)

/** 출발지, 도착지, 경유지의 좌표 정보를 제공합니다. */
data class ResponsePositionFormat(
    val location: List<Double>, // 경도, 위도로 된 좌표 정보를 나타냅니다. Length 2인 1차원 배열로, 경도, 위도 순서로 제공합니다(lng, lat).
    val dir: Int?               // 경로상에서 location 좌표를 바라보는 방향. 경유지와 목적지에 대해서만 존재할 수 있습니다.
)
