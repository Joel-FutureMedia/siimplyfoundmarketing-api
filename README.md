# Email Marketing System

A complete newsletter email marketing system with Spring Boot backend and React frontend.

## Project Structure

```
Email/
├── emailmarketapi/          # Backend (Spring Boot)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/simplyfound/emailmarketapi/
│   │   │   │   ├── Models/
│   │   │   │   ├── Controllers/
│   │   │   │   ├── Services/
│   │   │   │   ├── Repositories/
│   │   │   │   └── Config/
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── pom.xml
│   └── uploads/             # Media files storage
│
└── emailmarket/             # Frontend (React + Vite)
    ├── src/
    │   ├── components/
    │   ├── pages/
    │   ├── services/
    │   └── App.jsx
    ├── package.json
    └── vite.config.js
```

## Prerequisites

- Java 21
- Maven 3.6+
- Node.js 18+
- PostgreSQL 12+
- Postman (for API testing)

## Backend Setup (emailmarketapi)

### 1. Database Setup

Create PostgreSQL database:

```sql
CREATE DATABASE simplyfound;
```

### 2. Update Configuration

Edit `src/main/resources/application.properties` if needed:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/simplyfound
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 3. Build and Run

```bash
cd emailmarketapi
mvn clean install
mvn spring-boot:run
```

Backend will run on `http://localhost:8080`

### 4. Verify Backend is Running

Open browser or use curl:
```bash
curl http://localhost:8080/api/subscribers/count
```

Expected response:
```json
{
  "totalSubscribed": 0,
  "totalUnsubscribed": 0
}
```

## Frontend Setup (emailmarket)

### 1. Install Dependencies

```bash
cd emailmarket
npm install
```

### 2. Run Development Server

```bash
npm run dev
```

Frontend will run on `http://localhost:5173`

### 3. Verify Frontend is Running

Open browser: `http://localhost:5173`

You should see the Dashboard page.

## Testing Guide

### Backend API Testing

#### Option 1: Using Postman Collection

1. **Import Postman Collection:**
   - Open Postman
   - Click "Import" button
   - Select `EmailMarketingAPI.postman_collection.json`
   - All endpoints will be imported with sample data

2. **Set Environment Variables (Optional):**
   - Create a new environment in Postman
   - Add variable: `base_url` = `http://localhost:8080`
   - Use `{{base_url}}` in requests

3. **Test Endpoints in Order:**
   - Start with Subscriber endpoints
   - Then Newsletter endpoints
   - Finally Analytics endpoints

#### Option 2: Using cURL Commands

**1. Subscribe an Email:**
```bash
curl -X POST "http://localhost:8080/api/subscribers/subscribe?email=test@example.com"
```

**2. Get All Subscribers:**
```bash
curl -X GET "http://localhost:8080/api/subscribers/all"
```

**3. Get Subscriber Counts:**
```bash
curl -X GET "http://localhost:8080/api/subscribers/count"
```

**4. Create Newsletter (with file upload):**
```bash
curl -X POST "http://localhost:8080/api/newsletters/create" \
  -F "title=Welcome Newsletter" \
  -F "subtitle=Welcome to our service" \
  -F "content=<h1>Welcome!</h1><p>Thank you for subscribing.</p>" \
  -F "mediaFile=@/path/to/image.jpg"
```

**5. Get All Newsletters:**
```bash
curl -X GET "http://localhost:8080/api/newsletters/all"
```

**6. Send Newsletter:**
```bash
curl -X POST "http://localhost:8080/api/newsletters/send/1"
```

**7. Get Newsletter Analytics:**
```bash
curl -X GET "http://localhost:8080/api/analytics/1"
```

**8. Unsubscribe Email:**
```bash
curl -X GET "http://localhost:8080/api/subscribers/unsubscribe?email=test@example.com"
```

#### Option 3: Manual Testing via Frontend

1. **Test Subscriber Management:**
   - Navigate to `/subscribers` page
   - View subscriber list
   - Test search functionality
   - Export CSV

2. **Test Newsletter Creation:**
   - Navigate to `/create-newsletter`
   - Fill in title, subtitle, content
   - Upload an image or video
   - Click "Create Newsletter"
   - Verify newsletter appears in history

3. **Test Newsletter Sending:**
   - Go to `/newsletters` page
   - Click "Send Now" on a draft newsletter
   - Check email inbox for received newsletter
   - Verify email opens tracking pixel

4. **Test Analytics:**
   - Navigate to `/analytics`
   - Select a newsletter
   - View charts and statistics
   - Verify open rate tracking

### Complete Testing Workflow

#### Step 1: Subscribe Multiple Emails

```bash
# Subscribe first email
curl -X POST "http://localhost:8080/api/subscribers/subscribe?email=user1@example.com"

# Subscribe second email
curl -X POST "http://localhost:8080/api/subscribers/subscribe?email=user2@example.com"

# Subscribe third email
curl -X POST "http://localhost:8080/api/subscribers/subscribe?email=user3@example.com"

# Verify subscribers
curl -X GET "http://localhost:8080/api/subscribers/all"
```

**Expected Result:** All three emails should be subscribed.

#### Step 2: Create Newsletter

```bash
curl -X POST "http://localhost:8080/api/newsletters/create" \
  -F "title=Monthly Newsletter" \
  -F "subtitle=December 2024 Edition" \
  -F "content=<h2>Hello Subscribers!</h2><p>This is our monthly newsletter with exciting updates.</p><ul><li>New Features</li><li>Upcoming Events</li><li>Special Offers</li></ul>" \
  -F "mediaFile=@test-image.jpg"
```

**Expected Result:** Newsletter created with ID returned.

#### Step 3: Send Newsletter

```bash
# Replace {id} with actual newsletter ID from step 2
curl -X POST "http://localhost:8080/api/newsletters/send/1"
```

**Expected Result:** Newsletter sent to all active subscribers.

#### Step 4: Check Email Inbox

- Open email inboxes for subscribed emails
- Verify newsletter received
- Check email formatting (logo, styling, content)
- Click unsubscribe link to test unsubscribe

#### Step 5: Track Email Opens

- Open the newsletter email
- The tracking pixel should fire automatically
- Check analytics:

```bash
curl -X GET "http://localhost:8080/api/analytics/1"
```

**Expected Result:** Analytics show opened emails.

#### Step 6: Test Unsubscribe

```bash
curl -X GET "http://localhost:8080/api/subscribers/unsubscribe?email=user1@example.com"
```

**Expected Result:** Email unsubscribed successfully.

#### Step 7: Verify Analytics Dashboard

- Navigate to frontend `/analytics` page
- View charts and statistics
- Verify open rates are displayed correctly

### Frontend Testing Checklist

#### Dashboard Page (`/`)
- [ ] Total subscribers count displays correctly
- [ ] Total unsubscribed count displays correctly
- [ ] Total emails sent count displays correctly
- [ ] Open rate displays correctly
- [ ] Newsletter count displays correctly
- [ ] Cards are responsive on mobile

#### Subscribers Page (`/subscribers`)
- [ ] Subscriber list loads correctly
- [ ] Search functionality works
- [ ] Delete subscriber works
- [ ] CSV export works
- [ ] Status badges display correctly (Subscribed/Unsubscribed)
- [ ] Table is responsive

#### Create Newsletter Page (`/create-newsletter`)
- [ ] Form validation works (required fields)
- [ ] File upload works (images and videos)
- [ ] Preview displays correctly
- [ ] Active subscriber count displays
- [ ] Newsletter creation succeeds
- [ ] Redirect to newsletters page after creation

#### Newsletter History Page (`/newsletters`)
- [ ] Newsletter list loads correctly
- [ ] Draft/Sent status displays correctly
- [ ] Media preview works
- [ ] Analytics data displays for sent newsletters
- [ ] Send newsletter button works
- [ ] Delete newsletter works
- [ ] View analytics link works

#### Analytics Page (`/analytics`)
- [ ] Newsletter selector works
- [ ] Charts render correctly
- [ ] Statistics display correctly
- [ ] Open rate calculation is accurate
- [ ] Charts are responsive

### API Response Testing

#### Test Error Handling

**1. Duplicate Subscription:**
```bash
curl -X POST "http://localhost:8080/api/subscribers/subscribe?email=test@example.com"
curl -X POST "http://localhost:8080/api/subscribers/subscribe?email=test@example.com"
```
**Expected:** Second request returns error: "Email is already subscribed"

**2. Invalid Email Format:**
```bash
curl -X POST "http://localhost:8080/api/subscribers/subscribe?email=invalid-email"
```
**Expected:** Validation error

**3. Send Newsletter with No Subscribers:**
```bash
# Delete all subscribers first, then try to send
curl -X POST "http://localhost:8080/api/newsletters/send/1"
```
**Expected:** Error: "No active subscribers found"

**4. Unsubscribe Non-existent Email:**
```bash
curl -X GET "http://localhost:8080/api/subscribers/unsubscribe?email=nonexistent@example.com"
```
**Expected:** Error: "Subscriber not found"

### Database Verification

Connect to PostgreSQL and verify data:

```sql
-- Check subscribers
SELECT * FROM subscribers;

-- Check newsletters
SELECT * FROM newsletters;

-- Check email analytics
SELECT * FROM email_analytics;

-- Count opened emails for a newsletter
SELECT COUNT(*) FROM email_analytics 
WHERE newsletter_id = 1 AND opened = true;
```

### Email Testing

#### Test Email Delivery

1. **Use Real Email Addresses:**
   - Subscribe with your real email
   - Create and send newsletter
   - Check inbox (including spam folder)
   - Verify email formatting

2. **Test Email Template:**
   - Logo displays correctly
   - Title and subtitle are styled
   - Content renders properly
   - Media (image/video) displays
   - Unsubscribe button works
   - Footer information is correct

3. **Test Tracking Pixel:**
   - Open email
   - Check browser network tab for tracking request
   - Verify analytics updated

### Performance Testing

#### Test Bulk Email Sending

```bash
# Subscribe 100 emails (use script)
for i in {1..100}; do
  curl -X POST "http://localhost:8080/api/subscribers/subscribe?email=user$i@example.com"
done

# Create newsletter
curl -X POST "http://localhost:8080/api/newsletters/create" \
  -F "title=Test Bulk Send" \
  -F "subtitle=Testing performance" \
  -F "content=Test content"

# Send to all subscribers
curl -X POST "http://localhost:8080/api/newsletters/send/{id}"
```

**Expected:** All emails sent asynchronously without blocking.

### Troubleshooting

#### Backend Issues

**Problem:** Cannot connect to database
- **Solution:** Check PostgreSQL is running and credentials are correct

**Problem:** File upload fails
- **Solution:** Ensure `uploads/` directory exists and has write permissions

**Problem:** Email not sending
- **Solution:** Verify SMTP configuration in `application.properties`

#### Frontend Issues

**Problem:** API calls failing
- **Solution:** Ensure backend is running on port 8080
- **Solution:** Check CORS configuration
- **Solution:** Verify API base URL in `src/services/api.js`

**Problem:** Charts not displaying
- **Solution:** Ensure Chart.js dependencies are installed
- **Solution:** Check browser console for errors

## API Endpoints Reference

### Subscribers
- `POST /api/subscribers/subscribe?email={email}` - Subscribe email
- `GET /api/subscribers/unsubscribe?email={email}` - Unsubscribe email
- `GET /api/subscribers/all` - Get all subscribers
- `GET /api/subscribers/count` - Get subscriber counts
- `DELETE /api/subscribers/{id}` - Delete subscriber

### Newsletters
- `POST /api/newsletters/create` - Create newsletter (multipart/form-data)
  - Parameters: `title`, `subtitle`, `content`, `mediaFile`
- `POST /api/newsletters/send/{id}` - Send newsletter
- `PUT /api/newsletters/update/{id}` - Update newsletter
- `DELETE /api/newsletters/delete/{id}` - Delete newsletter
- `GET /api/newsletters/all` - Get all newsletters
- `GET /api/newsletters/analytics` - Get analytics summary

### Analytics
- `GET /api/track/{newsletterId}/{email}` - Track email open (pixel)
- `GET /api/analytics/{newsletterId}` - Get newsletter analytics
- `GET /api/analytics/dashboard` - Get dashboard analytics

## Features

### Backend Features
- ✅ Subscriber management (subscribe/unsubscribe)
- ✅ Newsletter creation with media upload
- ✅ Bulk email sending with async processing
- ✅ Email tracking (open rate via pixel)
- ✅ Analytics and reporting
- ✅ File upload handling (images/videos)
- ✅ Professional HTML email templates
- ✅ CORS configuration
- ✅ Global exception handling

### Frontend Features
- ✅ Dashboard with statistics
- ✅ Subscriber management page
- ✅ Create newsletter with media upload
- ✅ Newsletter history and management
- ✅ Analytics dashboard with charts
- ✅ Modern, responsive UI with Tailwind CSS
- ✅ CSV export for subscribers

## Database Schema

### Subscribers Table
- id (Long, Primary Key)
- email (String, unique, not null)
- subscribed (Boolean, not null)
- subscribedAt (LocalDateTime)
- unsubscribedAt (LocalDateTime)

### Newsletters Table
- id (Long, Primary Key)
- title (String, not null)
- subtitle (String, not null)
- content (Text)
- mediaUrl (String)
- mediaType (ENUM: IMAGE, VIDEO)
- createdAt (LocalDateTime)
- sentAt (LocalDateTime)
- totalRecipients (Integer)

### EmailAnalytics Table
- id (Long, Primary Key)
- newsletterId (Long, not null)
- recipientEmail (String, not null)
- opened (Boolean, not null)
- openedAt (LocalDateTime)

## Email Configuration

The system uses SMTP configuration in `application.properties`. Update these values for your email server:

```properties
spring.mail.host=mail.simplyfound.com.na
spring.mail.port=587
spring.mail.username=info@simplyfound.com.na
spring.mail.password=Simplyfound@2026
spring.mail.from=info@simplyfound.com.na
```

## File Uploads

Uploaded media files are stored in the `uploads/` directory and served at `/uploads/{filename}`.

## Development Notes

- Backend uses Spring Boot 3.2.0 with Java 21
- Frontend uses React 18 with Vite
- Styling with Tailwind CSS
- Charts with Chart.js
- Email tracking via 1x1 transparent PNG pixel
- Async email sending for better performance

## Postman Collection

Import `EmailMarketingAPI.postman_collection.json` into Postman for easy API testing with pre-configured requests and sample data.

## License

This project is proprietary software.
