# Wifi Ile Yoklama Kontrol Sistemi

Projenin amaci; bir android cihazi yardimi ile bagli modem uzerindeki ogrencilerin temsil ettigi cihazlarin mac adreslerine erismek ve bu adresler ile ogretmen kotrolunde dersin yoklamasini almak. Mac adresi tespit edilemeyen veya cihazi olmayan kisilerin yoklamaya eklenmesi icin ise ayrica alternatif bir menununde eklenmistir. 

Sistem Android cihaz icin client, verilerin islenmesi ve veri tabanina kaydedilmesi icin ise Linux bir cihaz uzerinde python ile server seklinde olusmaktadir

## Android-Client

Yoklama alacak kisinin login olmasi, modeme bagli cihazlarin mac adreslerin taranmasi, dersin kodu ve akilli cihazi olmayan kisilerin yoklamaya eklenmesi gerceklenmistir.

## Server

### Flask Veri Tabani Kontrolu

Flask ve Flask admin ile projenin verilerini ORM ile veri tabanina eklemek, web arayuzunde kontrol etmek ve socket arayuzunde verileri rahat bir sekilde filtrelemek gibi amaclar icin kullanilmistir. Verilerin eklenmesi ve kontrolu Flask-admin ornek kod arayuzu ile saglanmistir.

#### Veri Tabani Iliskisi
![dbpatterns](http://i.hizliresim.com/X9pBA6.jpg)

### Python Socket Altyapisi
Python Socket yaratarak sonsuz dongu icerisinde veri bekleyen bir mekanizma olusturuldu. Kullanicilarin kontrolu olan login islemi icin veri tabaninda veri kontrolu saglandi ve client'e onay veya red bilgisi gonderildi; yaratilan socket istegi sonlandirildi ve tekrar socket baglantisi kabulu beklendi. 

Bu iterasyon ile mac adresi taramasi icin veri kontrolu ve verilerin buffer'da bekletilmesi gerceklendi. Son olarak ders ismi ve numara ile yoklamaya eklenen ogrenciler icin baglanti beklendi ve dogru sekilde veri alindiginda veri tabaninda yoklama alma islemi gerceklendi.


## Proje Gelistiricileri
* Tolgahan ÜZÜN
* Şeyda GÖKDOĞAN