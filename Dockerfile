FROM nginx:alpine

# Remove default static assets
RUN rm -rf /usr/share/nginx/html/*

# Create template directory for envsubst
RUN mkdir -p /etc/nginx/templates

# Copy custom Nginx configuration template for dynamic $PORT injection on Render
COPY nginx.conf.template /etc/nginx/templates/default.conf.template

# Copy landing web page
COPY web/ /usr/share/nginx/html/

# Copy the Android APK binary for direct download
COPY jarablusalyoum.apk /usr/share/nginx/html/jarablusalyoum.apk

# Default environment port for Render
ENV PORT=10000

EXPOSE 10000

CMD ["nginx", "-g", "daemon off;"]
