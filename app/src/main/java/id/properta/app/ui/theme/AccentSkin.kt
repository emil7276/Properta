package id.properta.app.ui.theme

import androidx.compose.ui.graphics.Color

data class AccentSkin(
    val id: String,
    val name: String,
    val primary: Color,
    val glow: Color
)

object PropertaAccentSkins {
    val all = listOf(
        AccentSkin("ocean", "Ocean Navy", Color(0xFF155EEF), Color(0xFF60A5FA)),
        AccentSkin("electric", "Electric Blue", Color(0xFF2563EB), Color(0xFF38BDF8)),
        AccentSkin("arctic", "Arctic Cyan", Color(0xFF0891B2), Color(0xFF67E8F9)),
        AccentSkin("indigo", "Royal Indigo", Color(0xFF4F46E5), Color(0xFF818CF8)),
        AccentSkin("violet", "Aurora Violet", Color(0xFF7C3AED), Color(0xFFC084FC)),
        AccentSkin("emerald", "Emerald", Color(0xFF059669), Color(0xFF6EE7B7)),
        AccentSkin("mint", "Mint", Color(0xFF0D9488), Color(0xFF5EEAD4)),
        AccentSkin("amber", "Amber", Color(0xFFD97706), Color(0xFFFCD34D)),
        AccentSkin("coral", "Coral", Color(0xFFE45858), Color(0xFFFDA4AF)),
        AccentSkin("slate", "Slate", Color(0xFF475569), Color(0xFFCBD5E1))
    )

    val default: AccentSkin
        get() = all.first()
}
