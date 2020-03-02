import org.jsoup.Jsoup

fun main(){
    print(Jsoup.connect("https://bintray.com/package/generalTab?pkgPath=/him188moe/mirai/mirai-core").execute().body())
}