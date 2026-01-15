FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine

# Install gettext for envsubst
RUN apk add --no-cache gettext

# Copy built files
COPY --from=build /app/dist /usr/share/nginx/html

# Copy nginx config template
COPY nginx.conf.template /etc/nginx/templates/default.conf.template

# Default API Gateway URL (can be overridden via environment variable)
ENV API_GATEWAY_URL=http://host.docker.internal:8080

EXPOSE 80

CMD ["/bin/sh", "-c", "envsubst '$$API_GATEWAY_URL' < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]
