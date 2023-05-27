FROM node:19.3.0-alpine3.17

ENV PATH /my_front/node_modules/.bin:$PATH

RUN mkdir my_front
WORKDIR /my_front

COPY package.json .
RUN npm i
COPY . .

CMD ["npm", "start"]
