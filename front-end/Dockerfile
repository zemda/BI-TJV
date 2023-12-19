FROM node:16-alpine

ENV APP_HOME /app
WORKDIR $APP_HOME

COPY package*.json $APP_HOME/

RUN npm install --production && rm -rf /root/.npm
COPY . $APP_HOME/

RUN npm run build
CMD ["npm", "run", "start"]