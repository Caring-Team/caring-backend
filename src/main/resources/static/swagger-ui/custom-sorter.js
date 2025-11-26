window.onload = function() {
    // springdoc가 기본으로 만든 UI 설정 가져오기
    const originalOnLoad = window.springdocOnLoad;

    window.springdocOnLoad = function() {
        if (originalOnLoad) {
            originalOnLoad();
        }

        // UI 인스턴스를 가져와 operationsSorter 커스터마이징
        if (window.ui) {
            window.ui.getConfigs().operationsSorter = function(a, b) {
                const summaryA = a.get("summary") || "";
                const summaryB = b.get("summary") || "";

                // "1. xxx" → 숫자 부분만 파싱
                const nA = parseInt(summaryA.split(".")[0], 10);
                const nB = parseInt(summaryB.split(".")[0], 10);

                if (!isNaN(nA) && !isNaN(nB)) {
                    return nA - nB;
                }

                // 숫자가 없으면 summary 알파벳 순
                return summaryA.localeCompare(summaryB);
            };
        }
    };
};