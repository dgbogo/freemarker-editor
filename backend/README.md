# Freemarker Editor

Freemarker Editor is a tool to help with editing Apache FreeMarker templates. It watches the folder where your template/data file is and hot reloads on every file change. It can render the template to HTML or PDF.

https://user-images.githubusercontent.com/19174237/175385412-1ddd0661-70e2-40c5-bd10-75fa4a6183f5.mp4

# Building and running

## Frontend

1. Change dir to the frontend Vue project: `cd frontend`

2. Install the dependencies: `npm run install`

3. Build the frontend: `npm run build`

## Backend

1. After building the frontend, you can run the backend which will serve everything: `mvn quarkus:dev`

2. The application will be accessible on http://localhost:8080
