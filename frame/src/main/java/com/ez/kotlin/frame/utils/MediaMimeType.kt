package com.ez.kotlin.frame.utils

import android.text.TextUtils
import com.ez.kotlin.frame.utils.ext

/**
 * @author : ezhuwx
 * Describe :MimeType
 * Designed on 2023/6/7
 * E-mail : ezhuwx@163.com
 * Update on 14:36 by ezhuwx
 */

/**
 * isGif
 */
fun String?.isGif(): Boolean {
    return this.mimeType() == MediaMimeType.GIF
}

/**
 * isVideo
 */
fun String?.isDocument(): Boolean {
    return mimeType().isDocument()
}

/**
 * isVideo
 */
fun MediaMimeType?.isDocument(): Boolean {
    return this?.type?.startsWith(MIME_TYPE_PREFIX_TEXT) == true
}

/**
 * isVideo
 */
fun String?.isVideo(): Boolean {
    return mimeType().isVideo()
}

/**
 * isVideo
 */
fun MediaMimeType?.isVideo(): Boolean {
    return this?.type?.startsWith(MIME_TYPE_PREFIX_VIDEO) == true
}

/**
 * isAudio
 */
fun String?.isAudio(): Boolean {
    return mimeType().isAudio()
}

/**
 * isAudio
 */
fun MediaMimeType?.isAudio(): Boolean {
    return this?.type?.startsWith(MIME_TYPE_PREFIX_AUDIO) == true
}

/**
 * isImage
 */
fun String?.isImage(): Boolean {
    return mimeType().isImage()
}

/**
 * isImage
 */
fun MediaMimeType?.isImage(): Boolean {
    return this?.type?.startsWith(MIME_TYPE_PREFIX_IMAGE) == true
}

/**
 * Picture or video
 *
 * @return
 */
fun String?.mimeType(): MediaMimeType? {
    MediaMimeType.values().forEach {
        if (it.ext == this.ext().removePrefix(".").lowercase()) {
            return it
        }
    }
    return null
}

const val MIME_TYPE_IMAGE = "image/*"
const val MIME_TYPE_VIDEO = "video/*"
const val MIME_TYPE_AUDIO = "audio/*"
const val MIME_TYPE_TEXT = "text/*"
const val MIME_TYPE_PREFIX_IMAGE = "image"
const val MIME_TYPE_PREFIX_VIDEO = "video"
const val MIME_TYPE_PREFIX_AUDIO = "audio"
const val MIME_TYPE_PREFIX_TEXT = "text"

/**
 * content url
 */
fun String?.isContent(): Boolean {
    return !isNullOrEmpty() && startsWith("content://")
}

enum class MediaMimeType(val type: String, val ext: String) {
    /**
     * 3GPP : 3GPP
     **/
    GPP("audio/3gpp", "3gpp"),

    /**
     * AMR : AMR
     **/
    AMR("audio/amr", "amr"),

    /**
     * BASIC : SND
     **/
    SND("audio/basic", "snd"),

    /**
     * MIDI : MID
     **/
    MID("audio/midi", "mid"),

    /**
     * MIDI : MIDI
     **/
    MIDI("audio/midi", "midi"),

    /**
     * MIDI : KAR
     **/
    KAR("audio/midi", "kar"),

    /**
     * MIDI : XMF
     **/
    XMF("audio/midi", "xmf"),

    /**
     * MOBILE-XMF : MXMF
     **/
    MXMF("audio/mobile-xmf", "mxmf"),

    /**
     * MPEG : MPGA
     **/
    MPGA("audio/mpeg", "mpga"),

    /**
     * MPEG : MPEGA
     **/
    MPEGA("audio/mpeg", "mpega"),

    /**
     * MPEG : MP2
     **/
    MP2("audio/mpeg", "mp2"),

    /**
     * MPEG : MP3
     **/
    MP3("audio/mpeg", "mp3"),

    /**
     * MPEG : M4A
     **/
    M4A("audio/mpeg", "m4a"),

    /**
     * MPEGURL : M3U
     **/
    M3U("audio/mpegurl", "m3u"),

    /**
     * PRS.SID : SID
     **/
    SID("audio/prs.sid", "sid"),

    /**
     * X-AIFF : AIF
     **/
    AIF("audio/x-aiff", "aif"),

    /**
     * X-AIFF : AIFF
     **/
    AIFF("audio/x-aiff", "aiff"),

    /**
     * X-AIFF : AIFC
     **/
    AIFC("audio/x-aiff", "aifc"),

    /**
     * X-GSM : GSM
     **/
    GSM("audio/x-gsm", "gsm"),

    /**
     * X-MPEGURL : M3U
     **/
    MPEGUR("audio/x-mpegurl", "m3u"),

    /**
     * X-MS-WMA : WMA
     **/
    WMA("audio/x-ms-wma", "wma"),

    /**
     * X-MS-WAX : WAX
     **/
    WAX("audio/x-ms-wax", "wax"),

    /**
     * X-PN-REALAUDIO : RA
     **/
    PN_RA("audio/x-pn-realaudio", "ra"),

    /**
     * X-PN-REALAUDIO : RM
     **/
    RM("audio/x-pn-realaudio", "rm"),

    /**
     * X-PN-REALAUDIO : RAM
     **/
    RAM("audio/x-pn-realaudio", "ram"),

    /**
     * X-REALAUDIO : RA
     **/
    RA("audio/x-realaudio", "ra"),

    /**
     * X-SCPLS : PLS
     **/
    PLS("audio/x-scpls", "pls"),

    /**
     * X-SD2 : SD2
     **/
    SD2("audio/x-sd2", "sd2"),

    /**
     * X-WAV : WAV
     **/
    WAV("audio/x-wav", "wav"),

    /**
     * BMP : BMP
     **/
    BMP("image/bmp", "bmp"),

    /**
     * X-QCP : QCP
     **/
    QCP("audio/x-qcp", "qcp"),

    /**
     * GIF : GIF
     **/
    GIF("image/gif", "gif"),

    /**
     * ICO : CUR
     **/
    CUR("image/ico", "cur"),

    /**
     * ICO : ICO
     **/
    ICO("image/ico", "ico"),

    /**
     * IEF : IEF
     **/
    IEF("image/ief", "ief"),

    /**
     * JPEG : JPEG
     **/
    JPEG("image/jpeg", "jpeg"),

    /**
     * JPEG : JPG
     **/
    JPG("image/jpeg", "jpg"),

    /**
     * JPEG : JPE
     **/
    JPE("image/jpeg", "jpe"),

    /**
     * PCX : PCX
     **/
    PCX("image/pcx", "pcx"),

    /**
     * PNG : PNG
     **/
    PNG("image/png", "png"),

    /**
     * SVG+XML : SVG
     **/
    SVG("image/svg+xml", "svg"),

    /**
     * SVG+XML : SVGZ
     **/
    SVGZ("image/svg+xml", "svgz"),

    /**
     * TIFF : TIFF
     **/
    TIFF("image/tiff", "tiff"),

    /**
     * TIFF : TIF
     **/
    TIF("image/tiff", "tif"),

    /**
     * VND.DJVU : DJVU
     **/
    DJVU("image/vnd.djvu", "djvu"),

    /**
     * VND.DJVU : DJV
     **/
    DJV("image/vnd.djvu", "djv"),

    /**
     * VND.WAP.WBMP : WBMP
     **/
    WBMP("image/vnd.wap.wbmp", "wbmp"),

    /**
     * X-CMU-RASTER : RAS
     **/
    RAS("image/x-cmu-raster", "ras"),

    /**
     * X-CORELDRAW : CDR
     **/
    CDR("image/x-coreldraw", "cdr"),

    /**
     * X-CORELDRAWPATTERN : PAT
     **/
    PAT("image/x-coreldrawpattern", "pat"),

    /**
     * X-CORELDRAWTEMPLATE : CDT
     **/
    CDT("image/x-coreldrawtemplate", "cdt"),

    /**
     * X-CORELPHOTOPAINT : CPT
     **/
    CPT("image/x-corelphotopaint", "cpt"),

    /**
     * X-ICON : ICO
     **/
    X_ICO("image/x-icon", "ico"),

    /**
     * X-JG : ART
     **/
    ART("image/x-jg", "art"),

    /**
     * X-JNG : JNG
     **/
    JNG("image/x-jng", "jng"),

    /**
     * X-MS-BMP : BMP
     **/
    MS_BMP("image/x-ms-bmp", "bmp"),

    /**
     * X-PHOTOSHOP : PSD
     **/
    PSD("image/x-photoshop", "psd"),

    /**
     * X-PORTABLE-ANYMAP : PNM
     **/
    PNM("image/x-portable-anymap", "pnm"),

    /**
     * X-PORTABLE-BITMAP : PBM
     **/
    PBM("image/x-portable-bitmap", "pbm"),

    /**
     * X-PORTABLE-GRAYMAP : PGM
     **/
    PGM("image/x-portable-graymap", "pgm"),

    /**
     * X-PORTABLE-PIXMAP : PPM
     **/
    PPM("image/x-portable-pixmap", "ppm"),

    /**
     * X-RGB : RGB
     **/
    RGB("image/x-rgb", "rgb"),

    /**
     * X-XBITMAP : XBM
     **/
    XBM("image/x-xbitmap", "xbm"),

    /**
     * X-XPIXMAP : XPM
     **/
    XPM("image/x-xpixmap", "xpm"),

    /**
     * X-XWINDOWDUMP : XWD
     **/
    XWD("image/x-xwindowdump", "xwd"),

    /**
     * IGES : IGS
     **/
    IGS("model/iges", "igs"),

    /**
     * IGES : IGES
     **/
    IGES("model/iges", "iges"),

    /**
     * MESH : MSH
     **/
    MSH("model/mesh", "msh"),

    /**
     * MESH : MESH
     **/
    MESH("model/mesh", "mesh"),

    /**
     * MESH : SILO
     **/
    SILO("model/mesh", "silo"),

    /**
     * CALENDAR : ICS
     **/
    ICS("text/calendar", "ics"),

    /**
     * CALENDAR : ICZ
     **/
    ICZ("text/calendar", "icz"),

    /**
     * COMMA-SEPARATED-VALUES : CSV
     **/
    CSV("text/comma-separated-values", "csv"),

    /**
     * CSS : CSS
     **/
    CSS("text/css", "css"),

    /**
     * HTML : HTM
     **/
    HTM("text/html", "htm"),

    /**
     * HTML : HTML
     **/
    HTML("text/html", "html"),

    /**
     * H323 : 323
     **/
    H323("text/h323", "323"),

    /**
     * IULS : ULS
     **/
    ULS("text/iuls", "uls"),

    /**
     * MATHML : MML
     **/
    MML("text/mathml", "mml"),

    /**
     * PLAIN : TXT
     **/
    TXT("text/plain", "txt"),

    /**
     * PLAIN : ASC
     **/
    ASC("text/plain", "asc"),

    /**
     * PLAIN : TEXT
     **/
    TEXT("text/plain", "text"),

    /**
     * PLAIN : DIFF
     **/
    DIFF("text/plain", "diff"),

    /**
     * PLAIN : PO
     **/
    PO("text/plain", "po"),

    /**
     * RICHTEXT : RTX
     **/
    RTX("text/richtext", "rtx"),

    /**
     * RTF : RTF
     **/
    RTF("text/rtf", "rtf"),

    /**
     * TEXMACS : TS
     **/
    TS("text/texmacs", "ts"),

    /**
     * TEXT : PHPS
     **/
    PHPS("text/text", "phps"),

    /**
     * TAB-SEPARATED-VALUES : TSV
     **/
    TSV("text/tab-separated-values", "tsv"),

    /**
     * XML : XML
     **/
    XML("text/xml", "xml"),

    /**
     * X-BIBTEX : BIB
     **/
    BIB("text/x-bibtex", "bib"),

    /**
     * X-BOO : BOO
     **/
    BOO("text/x-boo", "boo"),

    /**
     * X-C++HDR : H++
     **/
    HDR("text/x-c++hdr", "h++"),

    /**
     * X-C++HDR : HPP
     **/
    HPP("text/x-c++hdr", "hpp"),

    /**
     * X-C++HDR : HXX
     **/
    HXX("text/x-c++hdr", "hxx"),

    /**
     * X-C++HDR : HH
     **/
    HH("text/x-c++hdr", "hh"),

    /**
     * X-C++SRC : C++
     **/
    CSR("text/x-c++src", "c++"),

    /**
     * X-C++SRC : CPP
     **/
    CPP("text/x-c++src", "cpp"),

    /**
     * X-C++SRC : CXX
     **/
    CXX("text/x-c++src", "cxx"),

    /**
     * X-CHDR : H
     **/
    H("text/x-chdr", "h"),

    /**
     * X-COMPONENT : HTC
     **/
    HTC("text/x-component", "htc"),

    /**
     * X-CSH : CSH
     **/
    CSH("text/x-csh", "csh"),

    /**
     * X-CSRC : C
     **/
    C("text/x-csrc", "c"),

    /**
     * X-DSRC : D
     **/
    D("text/x-dsrc", "d"),

    /**
     * X-HASKELL : HS
     **/
    HS("text/x-haskell", "hs"),

    /**
     * X-JAVA : JAVA
     **/
    JAVA("text/x-java", "java"),

    /**
     * X-LITERATE-HASKELL : LHS
     **/
    LHS("text/x-literate-haskell", "lhs"),

    /**
     * X-MOC : MOC
     **/
    MOC("text/x-moc", "moc"),

    /**
     * X-PASCAL : P
     **/
    P("text/x-pascal", "p"),

    /**
     * X-PASCAL : PAS
     **/
    PAS("text/x-pascal", "pas"),

    /**
     * X-PCS-GCD : GCD
     **/
    GCD("text/x-pcs-gcd", "gcd"),

    /**
     * X-SETEXT : ETX
     **/
    ETX("text/x-setext", "etx"),

    /**
     * X-TCL : TCL
     **/
    TCL("text/x-tcl", "tcl"),

    /**
     * X-TEX : TEX
     **/
    TEX("text/x-tex", "tex"),

    /**
     * X-TEX : LTX
     **/
    LTX("text/x-tex", "ltx"),

    /**
     * X-TEX : STY
     **/
    STY("text/x-tex", "sty"),

    /**
     * X-TEX : CLS
     **/
    CLS("text/x-tex", "cls"),

    /**
     * X-VCALENDAR : VCS
     **/
    VCS("text/x-vcalendar", "vcs"),

    /**
     * X-VCARD : VCF
     **/
    VCF("text/x-vcard", "vcf"),

    /**
     * 3GPP : 3GPP
     **/
    V_GPP("video/3gpp", "3gpp"),

    /**
     * 3GPP : 3GP
     **/
    GP("video/3gpp", "3gp"),

    /**
     * 3GPP : 3G2
     **/
    G2("video/3gpp", "3g2"),

    /**
     * DL : DL
     **/
    DL("video/dl", "dl"),

    /**
     * DV : DIF
     **/
    DIF("video/dv", "dif"),

    /**
     * DV : DV
     **/
    DV("video/dv", "dv"),

    /**
     * FLI : FLI
     **/
    FLI("video/fli", "fli"),

    /**
     * M4V : M4V
     **/
    M4V("video/m4v", "m4v"),

    /**
     * MPEG : MPEG
     **/
    MPEG("video/mpeg", "mpeg"),

    /**
     * MPEG : MPG
     **/
    MPG("video/mpeg", "mpg"),

    /**
     * MPEG : MPE
     **/
    MPE("video/mpeg", "mpe"),

    /**
     * MP4 : MP4
     **/
    MP4("video/mp4", "mp4"),

    /**
     * MPEG : VOB
     **/
    VOB("video/mpeg", "VOB"),

    /**
     * QUICKTIME : QT
     **/
    QT("video/quicktime", "qt"),

    /**
     * QUICKTIME : MOV
     **/
    MOV("video/quicktime", "mov"),

    /**
     * VND.MPEGURL : MXU
     **/
    MXU("video/vnd.mpegurl", "mxu"),

    /**
     * WEBM : WEBM
     **/
    WEBM("video/webm", "webm"),

    /**
     * X-LA-ASF : LSF
     **/
    LSF("video/x-la-asf", "lsf"),

    /**
     * X-LA-ASF : LSX
     **/
    LSX("video/x-la-asf", "lsx"),

    /**
     * X-MNG : MNG
     **/
    MNG("video/x-mng", "mng"),

    /**
     * X-MS-ASF : ASF
     **/
    ASF("video/x-ms-asf", "asf"),

    /**
     * X-MS-ASF : ASX
     **/
    ASX("video/x-ms-asf", "asx"),

    /**
     * X-MS-WM : WM
     **/
    WM("video/x-ms-wm", "wm"),

    /**
     * X-MS-WMV : WMV
     **/
    WMV("video/x-ms-wmv", "wmv"),

    /**
     * X-MS-WMX : WMX
     **/
    WMX("video/x-ms-wmx", "wmx"),

    /**
     * X-MS-WVX : WVX
     **/
    WVX("video/x-ms-wvx", "wvx"),

    /**
     * X-MSVIDEO : AVI
     **/
    AVI("video/x-msvideo", "avi"),

    /**
     * X-SGI-MOVIE : MOVIE
     **/
    MOVIE("video/x-sgi-movie", "movie"),
}