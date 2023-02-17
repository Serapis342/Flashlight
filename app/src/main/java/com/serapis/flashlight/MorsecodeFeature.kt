package com.serapis.flashlight

@Suppress("SpellCheckingInspection")
class MorsecodeFeature {
    private val morseAlphabet = mapOf(
        "A" to ".-", "B" to "-...", "C" to "-.-.", "D" to "-..", "E" to ".", "F" to "..-.", "G" to "--.", "H" to "....",
        "I" to "..", "J" to ".---", "K" to "-.-", "L" to ".-..", "M" to "--", "N" to "-.", "O" to "---", "P" to ".--.",
        "Q" to "--.-", "R" to ".-.", "S" to "...", "T" to "-", "U" to "..-", "V" to "...-", "W" to ".--", "X" to "-..-",
        "Y" to "-.--", "Z" to "--..", "0" to "-----", "1" to ".----", "2" to "..---", "3" to "...--", "4" to "....-",
        "5" to ".....", "6" to "-....", "7" to "--...", "8" to "----.", "9" to "-----", "À" to ".--.-", "Å" to ".--.-",
        "Ä" to ".-.-", "È" to "..-..", "Ö" to "---.", "Ü" to "..--", "ß" to "...--..", ";" to "-.-.-.", "?" to "..--..",
        "-" to "-....-", "_" to "..--.-", "Ñ" to "--.--", "." to ".-.-.-", "," to "--..--", ":" to "---...", "(" to "-.--.",
        ")" to "-.--.-", "=" to "-...-", "+" to ".-.-.", "/" to "-..-.", "@" to ".--.-.", " " to " ")

    fun translateToMorse(clearText: String): String {
        var morseText: String? = ""
        for (i in 1..clearText.length) {
             morseText += morseAlphabet[clearText[i - 1].toString().uppercase()]
        }
        if(morseText!!.contains("null")) {
            return ""
        }
        return morseText.toString()
    }
}