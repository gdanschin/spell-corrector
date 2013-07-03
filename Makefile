build:
	mvn-hh clean package

clean:
	mvn-hh clean

install:
	umask 0222
	install -D target/correktor.jar debian/hh-correktor/usr/share/hh-correktor/hh-correktor.jar
	install -D src/etc/hh-correktor/settings.properties debian/hh-correktor/etc/hh-correktor/settings.properties
	install -D src/etc/hh-correktor/logback.xml debian/hh-correktor/etc/hh-correktor/logback.xml
	install -D src/etc/init/hh-correktor.conf debian/hh-correktor/etc/init/hh-correktor.conf
