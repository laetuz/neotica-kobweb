package id.neotica.neotica.domain.dummy

import id.neotica.neotica.pages.orpheum.TrackFeedResponse
import id.neotica.neotica.pages.orpheum.TrackRemoteModel

object OrpheumTrackList {
    private val allTracks = listOf(
        TrackRemoteModel("t-001", "a-001", "Neon Lights", 245, "https://example.com/audio/neon-lights.mp3", 1, "Ryo Martin"),
        TrackRemoteModel("t-002", "a-001", "Midnight Pulse", 198, "https://example.com/audio/midnight-pulse.mp3", 2, "Ryo Martin"),
        TrackRemoteModel("t-003", "a-002", "Velvet Sky", 312, "https://example.com/audio/velvet-sky.mp3", 1, "Galih Putro Aji"),
        TrackRemoteModel("t-004", "a-002", "Crimson Wave", 177, "https://example.com/audio/crimson-wave.mp3", 2, "Galih Putro Aji"),
        TrackRemoteModel("t-005", "a-003", "Empty Promises", 290, "https://example.com/audio/empty-promises.mp3", 1, "Ayu Krisna"),
        TrackRemoteModel("t-006", "a-003", "Fading Echo", 223, "https://example.com/audio/fading-echo.mp3", 2, "Ayu Krisna"),
        TrackRemoteModel("t-007", "a-004", "Static Dreams", 267, "https://example.com/audio/static-dreams.mp3", 1, "Marshella Vindriani"),
        TrackRemoteModel("t-008", "a-004", "Glass Horizon", 184, "https://example.com/audio/glass-horizon.mp3", 2, "Marshella Vindriani"),
        TrackRemoteModel("t-009", "a-005", "Ironclad", 335, "https://example.com/audio/ironclad.mp3", 1, "Gilang Swandaru"),
        TrackRemoteModel("t-010", "a-005", "Shattered Circuit", 156, "https://example.com/audio/shattered-circuit.mp3", 2, "Gilang Swandaru"),
        TrackRemoteModel("t-011", "a-006", "Whisper Code", 278, "https://example.com/audio/whisper-code.mp3", 1, "Retsa Aghnita"),
        TrackRemoteModel("t-012", "a-006", "Pixel Rain", 201, "https://example.com/audio/pixel-rain.mp3", 2, "Retsa Aghnita"),
        TrackRemoteModel("t-013", "a-007", "Driftwood", 244, "https://example.com/audio/driftwood.mp3", 1, "Pasya Gerhardien"),
        TrackRemoteModel("t-014", "a-007", "Chrome Cathedral", 319, "https://example.com/audio/chrome-cathedral.mp3", 2, "Pasya Gerhardien"),
        TrackRemoteModel("t-015", "a-008", "Solar Flare", 192, "https://example.com/audio/solar-flare.mp3", 1, "Edwin Yosua"),
        TrackRemoteModel("t-016", "a-008", "Nebula Drift", 261, "https://example.com/audio/nebula-drift.mp3", 2, "Edwin Yosua"),
        TrackRemoteModel("t-017", "a-009", "Binary Sunset", 287, "https://example.com/audio/binary-sunset.mp3", 1, "Ryo Martin"),
        TrackRemoteModel("t-018", "a-009", "Quantum Leap", 210, "https://example.com/audio/quantum-leap.mp3", 2, "Ryo Martin"),
        TrackRemoteModel("t-019", "a-010", "Phase Shift", 176, "https://example.com/audio/phase-shift.mp3", 1, "Galih Putro Aji"),
        TrackRemoteModel("t-020", "a-010", "Echo Chamber", 298, "https://example.com/audio/echo-chamber.mp3", 2, "Galih Putro Aji"),
        TrackRemoteModel("t-021", "a-011", "Pulse Wave", 233, "https://example.com/audio/pulse-wave.mp3", 1, "Ayu Krisna"),
        TrackRemoteModel("t-022", "a-011", "Hollow Frame", 189, "https://example.com/audio/hollow-frame.mp3", 2, "Ayu Krisna"),
        TrackRemoteModel("t-023", "a-012", "Arc Light", 304, "https://example.com/audio/arc-light.mp3", 1, "Marshella Vindriani"),
        TrackRemoteModel("t-024", "a-012", "Stray Signal", 165, "https://example.com/audio/stray-signal.mp3", 2, "Marshella Vindriani"),
        TrackRemoteModel("t-025", "a-013", "Undercurrent", 256, "https://example.com/audio/undercurrent.mp3", 1, "Gilang Swandaru"),
    )

    private const val PAGE_SIZE = 10

    fun getPage(page: Int): TrackFeedResponse {
        val clampedPage = page.coerceAtLeast(1)
        val start = (clampedPage - 1) * PAGE_SIZE
        val end = (start + PAGE_SIZE).coerceAtMost(allTracks.size)
        val data = if (start >= allTracks.size) emptyList() else allTracks.subList(start, end)
        val totalPages = (allTracks.size + PAGE_SIZE - 1) / PAGE_SIZE
        return TrackFeedResponse(
            data = data,
            page = clampedPage,
            limit = PAGE_SIZE,
            totalItems = allTracks.size,
            totalPages = totalPages
        )
    }
}
