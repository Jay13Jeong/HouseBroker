FROM nginx:1.25.3-alpine
##openssl start
RUN	apk add --no-cache openssl
RUN mkdir -p /etc/ssl
RUN openssl req -x509 \
-newkey rsa:2048 -nodes -out /etc/ssl/jjeong_ssl.pem -keyout /etc/ssl/jjeong_ssl.key \
-subj "/C=KR/ST=Seoul/L=Seoul/O=JJEONG/OU=jjeong/CN=jjeong/"
RUN	apk del openssl
##openssl end
COPY ./envsubst.sh /docker-entrypoint.d
