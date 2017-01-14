import socket
import datetime
from app import *

##Soket ayarlari
soket = socket.socket(socket.AF_INET,socket.SOCK_STREAM)

HOST = '0.0.0.0'
PORT = 8080
macaddress = []
ogrenciler = []

soket.bind((HOST,PORT))
soket.listen(1000)
##Soket ayarlari

#Sonsuz donguye soktum ki hep baglanti kursun
while True:
	print ('Kullanıcı bekleniyor')
	baglanti,adres = soket.accept()
	print ('Bir bağlantı kabul edildi.', adres)
##Baglanti kabul ettim ve baglantiniyi printledim

	data = baglanti.recv(1024).decode("utf-8") 
	print(data)

##Telefondan gelen user bilgisi	

	data = data.split("//")
	username=data[0].split("id:")
	password=data[1].split("password:")
##User ve Pass ayikladim

	login = User.query.filter(User.username == username[1]).first()
##Veritabaninda o isimde uye varmi dedim

##Uyenin pass'ini telefondan gelen pass ile karsilastirdim.
	if login.password == password[1]:
		baglanti.send(b'Yes')
		baglanti.close()
##yeni baglanti kabul ediyor.		

		baglanti,adres = soket.accept()
		data = baglanti.recv(1024).decode("utf-8") 

##gelen veri iceisinde mac kelimesi iceriyorsa mac adresidir ve 2. sayfadayizdir		
		if data.find('MAC:') != -1:
			
			for i in range(3,len(data.split()),4):
				temp=data.split()[i].replace(",","")
				temp=temp.replace("]","")
				macaddress.append(temp)
##MAc adreslerini ayikliyorum ve temp ekliyorum

			print(len(macaddress))	
			baglanti.send(b'Yes')
			baglanti.close()
		else:
			baglanti.send(b'no')
			baglanti.close()

##Yeni baglanti kabul ediyorum
		baglanti,adres = soket.accept()
		data = baglanti.recv(1024).decode("utf-8") 
		print(data)
##Ders id ve ogrenci numaralarini ayikliyorum.
		
		data = data.split("//")
		ders=data[0].split("ders:")[1]
		numara=data[1].split("numara:")[1]
		numara=numara.split(',')
##Ders id ile dersi buluyorum		
		dersbul= Ders.query.filter(Ders.number == ders).first()

##Ders varsa if giriyor
		if dersbul:
##Yoklama yapisini insa ediyorum
			yoklama=Post()
			yoklama.title.append(dersbul)
			yoklama.date= datetime.datetime.utcnow()
			yoklama.user=login
##Yoklama yapisina uye ders ismi ve o anki zamani ekliyorum

##Mac adresslerini taratiyorum ve ekliyorum			
			for i in range(0,len(macaddress)):
				try:
					ogr = Student.query.filter(Student.mac_adress == macaddress[i]).first()
					if ogr:
						yoklama.student.append(ogr)

				except:
					print('Hata')
##Ogrenci numaralarini taratiyor ve ekliyorum					
			for k in range(0,len(numara)):
				try:
					ogr = Student.query.filter(Student.number == numara[k]).first()
					if ogr:
						yoklama.student.append(ogr)
				except:
					print('olmadi')

##Veri tabanina ekliyorum
			db.session.add(yoklama)

##Veri tabanina gonderiyorum			
			try:
				db.session.commit()	
			except Exception as e:
				print('Error')
		baglanti.close()		
	else:
		baglanti.send(b'no')
		baglanti.close()


