# React 앱 빌드
FROM node:18 AS build
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build

# nginx로 정적 파일 서빙
FROM nginx:stable-alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
