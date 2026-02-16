# Testing Guide - Email Marketing System

## Quick Start Testing

### 1. Import Postman Collection

1. Open Postman application
2. Click **Import** button (top left)
3. Select `EmailMarketingAPI.postman_collection.json`
4. All endpoints will be imported with sample data

### 2. Verify Backend is Running

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

### 3. Verify Frontend is Running

Open browser: `http://localhost:5173`

## Postman Collection Structure

The Postman collection is organized into folders:

### 📁 Subscribers
- Subscribe Email
- Subscribe Multiple Emails
- Get All Subscribers
- Get Subscriber Counts
- Unsubscribe Email
- Delete Subscriber

### 📁 Newsletters
- Create Newsletter (Text Only)
- Create Newsletter (With Image)
- Create Newsletter (With Video)
- Get All Newsletters
- Send Newsletter
- Update Newsletter
- Delete Newsletter
- Get Newsletter Analytics Summary

### 📁 Analytics
- Track Email Open (Pixel)
- Get Newsletter Analytics
- Get Dashboard Analytics

### 📁 Testing Scenarios
- Complete Workflow Test (8 steps)
- Error Handling Tests

## Step-by-Step Testing Workflow

### Phase 1: Subscriber Management

**1. Subscribe First Email**
- Use Postman: `Subscribers > Subscribe Email`
- Email: `john.doe@example.com`
- Expected: Success response with subscriber data

**2. Subscribe More Emails**
- Use Postman: `Subscribers > Subscribe Multiple Emails`
- Email: `jane.smith@example.com`
- Repeat for 3-5 test emails

**3. Verify Subscribers**
- Use Postman: `Subscribers > Get All Subscribers`
- Expected: List of all subscribed emails

**4. Check Counts**
- Use Postman: `Subscribers > Get Subscriber Counts`
- Expected: Count of subscribed/unsubscribed

### Phase 2: Newsletter Creation

**1. Create Text-Only Newsletter**
- Use Postman: `Newsletters > Create Newsletter (Text Only)`
- Fill in title, subtitle, content
- Expected: Newsletter created with ID

**2. Create Newsletter with Image**
- Use Postman: `Newsletters > Create Newsletter (With Image)`
- Upload an image file (jpg, png)
- Expected: Newsletter created with media URL

**3. Create Newsletter with Video**
- Use Postman: `Newsletters > Create Newsletter (With Video)`
- Upload a video file (mp4)
- Expected: Newsletter created with video URL

**4. View All Newsletters**
- Use Postman: `Newsletters > Get All Newsletters`
- Expected: List of all newsletters

### Phase 3: Sending Newsletters

**1. Send Newsletter**
- Use Postman: `Newsletters > Send Newsletter`
- Update newsletter ID in URL (e.g., `/send/1`)
- Expected: Success message, emails sent

**2. Check Email Inbox**
- Open email inboxes for subscribed addresses
- Verify newsletter received
- Check email formatting

**3. Test Email Template**
- Verify logo displays
- Check title and subtitle styling
- Verify content renders correctly
- Check media displays (if uploaded)
- Test unsubscribe button

### Phase 4: Analytics & Tracking

**1. Simulate Email Open**
- Use Postman: `Analytics > Track Email Open (Pixel)`
- Update newsletter ID and email in URL
- Expected: 1x1 PNG image returned

**2. Check Newsletter Analytics**
- Use Postman: `Analytics > Get Newsletter Analytics`
- Update newsletter ID in URL
- Expected: Analytics data with open rate

**3. Check Dashboard Analytics**
- Use Postman: `Analytics > Get Dashboard Analytics`
- Expected: Overall statistics

**4. Verify Open Rate**
- Open multiple emails
- Call tracking pixel for each
- Check analytics again
- Expected: Open rate increases

### Phase 5: Unsubscribe Testing

**1. Unsubscribe Email**
- Use Postman: `Subscribers > Unsubscribe Email`
- Email: `john.doe@example.com`
- Expected: Success message

**2. Verify Unsubscribe**
- Use Postman: `Subscribers > Get All Subscribers`
- Expected: Email marked as unsubscribed

**3. Try Resubscribing**
- Use Postman: `Subscribers > Subscribe Email`
- Same email address
- Expected: Email resubscribed

## Frontend Testing Checklist

### Dashboard Page (`/`)
- [ ] Page loads without errors
- [ ] Statistics cards display correctly
- [ ] Numbers match backend data
- [ ] Responsive on mobile devices

### Subscribers Page (`/subscribers`)
- [ ] Subscriber list loads
- [ ] Search functionality works
- [ ] Delete button works
- [ ] CSV export downloads file
- [ ] Status badges display correctly

### Create Newsletter Page (`/create-newsletter`)
- [ ] Form validation works
- [ ] File upload works (images)
- [ ] File upload works (videos)
- [ ] Preview displays correctly
- [ ] Newsletter creation succeeds
- [ ] Redirects after creation

### Newsletter History (`/newsletters`)
- [ ] Newsletter list displays
- [ ] Draft/Sent status shows
- [ ] Media previews work
- [ ] Send button works
- [ ] Delete button works
- [ ] Analytics link works

### Analytics Page (`/analytics`)
- [ ] Newsletter selector works
- [ ] Charts render correctly
- [ ] Statistics display accurately
- [ ] Charts are responsive

## Error Testing

### Test Duplicate Subscription
1. Subscribe same email twice
2. Expected: Error message on second attempt

### Test Invalid Email
1. Try subscribing with invalid email format
2. Expected: Validation error

### Test Unsubscribe Non-existent
1. Try unsubscribing email that doesn't exist
2. Expected: Error message

### Test Send Without Subscribers
1. Delete all subscribers
2. Try sending newsletter
3. Expected: Error message

### Test Invalid Newsletter ID
1. Try to send/update/delete non-existent newsletter
2. Expected: Error message

## Database Verification

### Connect to PostgreSQL

```sql
-- Check subscribers
SELECT * FROM subscribers ORDER BY subscribed_at DESC;

-- Check newsletters
SELECT * FROM newsletters ORDER BY created_at DESC;

-- Check email analytics
SELECT * FROM email_analytics ORDER BY opened_at DESC;

-- Count opened emails for newsletter ID 1
SELECT 
    COUNT(*) as total_sent,
    SUM(CASE WHEN opened = true THEN 1 ELSE 0 END) as total_opened,
    ROUND(100.0 * SUM(CASE WHEN opened = true THEN 1 ELSE 0 END) / COUNT(*), 2) as open_rate
FROM email_analytics
WHERE newsletter_id = 1;
```

## Performance Testing

### Bulk Email Test

1. **Subscribe 50+ Emails:**
   ```bash
   for i in {1..50}; do
     curl -X POST "http://localhost:8080/api/subscribers/subscribe?email=user$i@test.com"
   done
   ```

2. **Create Newsletter:**
   ```bash
   curl -X POST "http://localhost:8080/api/newsletters/create" \
     -F "title=Bulk Test" \
     -F "subtitle=Testing Performance" \
     -F "content=Test content"
   ```

3. **Send Newsletter:**
   ```bash
   curl -X POST "http://localhost:8080/api/newsletters/send/1"
   ```

4. **Expected:** All emails sent asynchronously without blocking

## Email Testing Tips

### Use Real Email Addresses
- Subscribe with your actual email
- Check inbox (including spam folder)
- Verify email formatting
- Test unsubscribe link

### Test Email Template Elements
- ✅ Logo displays correctly
- ✅ Title is large and bold
- ✅ Subtitle is styled
- ✅ Content renders properly
- ✅ Media displays (image/video)
- ✅ Footer information correct
- ✅ Unsubscribe button works
- ✅ Tracking pixel loads

### Test Tracking Pixel
1. Open email
2. Check browser developer tools > Network tab
3. Look for request to `/api/track/...`
4. Verify analytics updated

## Troubleshooting

### Backend Not Starting
- Check PostgreSQL is running
- Verify database credentials
- Check port 8080 is available
- Review application logs

### Frontend Not Connecting
- Verify backend is running
- Check CORS configuration
- Verify API base URL
- Check browser console for errors

### Emails Not Sending
- Verify SMTP configuration
- Check email server credentials
- Review application logs
- Test SMTP connection

### File Upload Failing
- Ensure `uploads/` directory exists
- Check write permissions
- Verify file size limits
- Check file type validation

## Sample Test Data

### Sample Emails
- `test1@example.com`
- `test2@example.com`
- `user@test.com`
- `admin@example.com`

### Sample Newsletter Content
```html
<h2>Welcome!</h2>
<p>Thank you for subscribing to our newsletter.</p>
<ul>
  <li>Feature 1</li>
  <li>Feature 2</li>
  <li>Feature 3</li>
</ul>
<p>Stay tuned for more updates!</p>
```

### Sample Newsletter Titles
- "Welcome Newsletter"
- "Monthly Update - December 2024"
- "Product Launch Announcement"
- "Holiday Special Edition"

## Postman Collection Usage Tips

1. **Use Variables:** Set `base_url` variable to `http://localhost:8080`
2. **Save Responses:** Save responses to compare results
3. **Use Collections:** Run complete workflow using collection runner
4. **Update IDs:** Replace placeholder IDs with actual IDs from responses
5. **Test Sequences:** Use "Testing Scenarios" folder for complete workflows

## Success Criteria

✅ All API endpoints return expected responses
✅ Frontend pages load without errors
✅ Emails are sent successfully
✅ Email tracking works correctly
✅ Analytics display accurate data
✅ Unsubscribe functionality works
✅ File uploads work for images and videos
✅ Error handling works correctly
✅ Database stores data correctly

