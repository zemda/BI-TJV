FROM node:16

ENV APP_HOME /app
WORKDIR $APP_HOME

COPY package*.json $APP_HOME/

RUN npm install
COPY . $APP_HOME/

RUN npm run build
CMD ["npm", "run", "start"]