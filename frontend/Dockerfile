# 1. Base image
FROM node:20-alpine

# 2. Set the working directory
WORKDIR /app

# 3. Copy package files first for caching
COPY package*.json ./

# 4. Install dependencies
RUN npm install

# 5. Copy the rest of the application source code
COPY . .

# 6. Expose port 3000 to match docker-compose mapping
EXPOSE 3000

# 7. Start the development server on port 3000 and expose it to the network
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0", "--port", "3000"]
