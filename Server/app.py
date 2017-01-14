import os
import os.path as op
import socket
import gevent

from flask import Flask
from flask_sqlalchemy import SQLAlchemy

from wtforms import validators

import flask_admin as admin
from flask_admin.contrib import sqla
from flask_admin.contrib.sqla import filters


# Create application
app = Flask(__name__)

# Create dummy secrey key so we can use sessions
app.config['SECRET_KEY'] = '123456790'

# Create in-memory database
app.config['DATABASE_FILE'] = 'sample_db.sqlite'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + app.config['DATABASE_FILE']
app.config['SQLALCHEMY_ECHO'] = True
db = SQLAlchemy(app)


# Create models
class User(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	first_name = db.Column(db.String(100))
	last_name = db.Column(db.String(100))
	username = db.Column(db.String(80), unique=True)
	password = db.Column(db.String(80))
	email = db.Column(db.String(120), unique=True)

	def __str__(self):
		return self.username

	def __repr__(self):
		return '<User %r>' % (self.username)


# Create M2M table
post_student_table = db.Table('post_tags', db.Model.metadata,
	db.Column('post_id', db.Integer, db.ForeignKey('post.id')),
	db.Column('student_id', db.Integer, db.ForeignKey('student.id'))
	)

post_ders_table = db.Table('post_ders', db.Model.metadata,
	db.Column('post_id', db.Integer, db.ForeignKey('post.id')),
	db.Column('ders_id', db.Integer, db.ForeignKey('ders.id'))
	)


class Post(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	title = db.relationship('Ders', secondary=post_ders_table)
	date = db.Column(db.DateTime)

	user_id = db.Column(db.Integer(), db.ForeignKey(User.id))
	user = db.relationship(User, backref='posts')
	student = db.relationship('Student', secondary=post_student_table)

	def __str__(self):
		return self.title


class Student(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	name = db.Column(db.Unicode(64))
	number = db.Column(db.String(120))
	mac_adress = db.Column(db.String(120))

	def __str__(self):
		return self.name

	def __repr__(self):
		return '<Student %r>' % (self.name)

class Ders(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	name = db.Column(db.Unicode(64))
	number =db.Column(db.Unicode(64))

	def __str__(self):
		return self.name


class UserInfo(db.Model):
	id = db.Column(db.Integer, primary_key=True)

	key = db.Column(db.String(64), nullable=False)
	value = db.Column(db.String(64))

	user_id = db.Column(db.Integer(), db.ForeignKey(User.id))
	user = db.relationship(User, backref='info')

	def __str__(self):
		return '%s - %s' % (self.key, self.value)





# Flask views
@app.route('/')
def index():
	return '<a href="/admin/">Click me to get to Admin!</a>'


# Customized User model admin
class UserAdmin(sqla.ModelView):
	inline_models = (UserInfo,)


# Customized Post model admin
class PostAdmin(sqla.ModelView):
	# Visible columns in the list view

	# List of columns that can be sorted. For 'user' column, use User.username as
	# a column.
	column_sortable_list = ('title', ('user', 'user.username'), 'date')

	# Rename 'title' columns to 'Post Title' in list view


	
	# Pass arguments to WTForms. In this case, change label for text field to
	# be 'Big Text' and add required() validator.
   

	form_ajax_refs = {
		'user': {
			'fields': (User.username, User.email)
		},
		'student': {
			'fields': (Student.name,)
		}
	}

	def __init__(self, session):
		# Just call parent class with predefined model.
		super(PostAdmin, self).__init__(Post, session)





# Create admin
admin = admin.Admin(app, name='Tasarim Projesi', template_mode='bootstrap3')

# Add views
admin.add_view(UserAdmin(User, db.session))
admin.add_view(sqla.ModelView(Student, db.session))
admin.add_view(sqla.ModelView(Ders, db.session))
admin.add_view(PostAdmin(db.session))



def build_sample_db():
	db.drop_all()
	db.create_all()
	db.session.commit()


def server():

	soket = socket.socket(socket.AF_INET,socket.SOCK_STREAM)

	HOST = '0.0.0.0'
	PORT = 8080

	soket.bind((HOST,PORT))


	soket.listen(1000)

	while True:
		print ('Kullanıcı bekleniyor')
		baglanti,adres = soket.accept()
		print ('Bir bağlantı kabul edildi.', adres)


		data = baglanti.recv(1024).decode("utf-8") 
		print(data)
		data = data.split("//")
		data=data[0].split("id:")
		
		if data[1] == 'tolga':
			baglanti.send(b'Yes')
			baglanti.close()

			baglanti,adres = soket.accept()
			data = baglanti.recv(1024).decode("utf-8") 
			print(data)
			if data.find('MAC:') != -1:
				print ("lol")
				baglanti.send(b'Yes')
			else:
				baglanti.send(b'no')
			baglanti.close()
				
		else:
			baglanti.send(b'no')
			baglanti.close()


if __name__ == '__main__':
	# Build a sample db on the fly, if one does not exist yet.
	app_dir = op.realpath(os.path.dirname(__file__))
	database_path = op.join(app_dir, app.config['DATABASE_FILE'])
	if not os.path.exists(database_path):
		build_sample_db()

	
	app.run(debug=True)

	
