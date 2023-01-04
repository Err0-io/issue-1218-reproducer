FROM ubuntu:jammy
#RUN bash -c "sed -i -e 's%http://archive.ubuntu.com/ubuntu%mirror://mirrors.ubuntu.com/mirrors.txt%' -e 's/^deb-src/#deb-src/' /etc/apt/sources.list"
RUN bash -c 'apt-get update || true'
RUN bash -c 'apt-get -y install debconf openssl ca-certificates'
RUN bash -c 'apt-get update'
RUN bash -c 'echo "debian-installer debian-installer/locale string en_GB.UTF-8" | debconf-set-selections'
RUN bash -c 'echo "keyboard-configuration keyboard-configuration/xkb-keymap select us" | debconf-set-selections'
RUN bash -c 'apt-get upgrade -y'
RUN bash -c 'DEBIAN_FRONTEND=noninteractive apt-get install -y apt-transport-https wget curl less vim telnet host dbus syslog-ng tzdata gnupg2 locales language-pack-en dos2unix'
RUN bash -c 'rm -f /etc/localtime; ln -s /usr/share/zoneinfo/UTC /etc/localtime'
RUN bash -c 'echo Etc/UTC >/etc/timezone'
RUN bash -c 'dpkg-reconfigure --frontend noninteractive tzdata'
RUN bash -c 'dpkg-reconfigure --frontend noninteractive locales'
RUN bash -c 'echo "*     soft   nofile  131072" >> /etc/security/limits.conf'
RUN bash -c 'echo "*     hard   nofile  131072" >> /etc/security/limits.conf'
COPY prestart.sh /root/
COPY setup.sh /root/
COPY start.sh /root/
RUN bash -c 'chmod a+x /root/prestart.sh /root/setup.sh /root/start.sh'
RUN bash -c 'dos2unix /root/prestart.sh; dos2unix /root/setup.sh; dos2unix /root/start.sh'
RUN bash -c 'DEBIAN_FRONTEND=noninteractive apt-get install -y postgresql postgresql-contrib'
RUN bash -c 'chmod a+x /root/setup.sh /root/start.sh'
RUN bash -c 'dos2unix /root/setup.sh; dos2unix /root/start.sh'

COPY pg_hba.conf /etc/postgresql/14/main/
COPY postgresql.conf /etc/postgresql/14/main/

COPY schema.sql /tmp/

EXPOSE 5555
CMD /bin/sh -c /root/start.sh
