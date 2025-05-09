# Vite 앱 빌드
FROM node:18 AS build
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build

# nginx로 정적 파일 서빙
FROM nginx:stable-alpine

COPY --from=build /app/dist /usr/share/nginx/html
# nginx 설정 추가
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
