const puppeteer = require('puppeteer-extra');
const StealthPlugin = require('puppeteer-extra-plugin-stealth');

puppeteer.use(StealthPlugin());

(async () => {
    let browser;
    try {
        // Launch browser
        browser = await puppeteer.launch({
            headless: "new", // Ensures better detection avoidance
            args: ['--no-sandbox', '--disable-setuid-sandbox'],
            executablePath: puppeteer.executablePath()
        });

        const page = await browser.newPage();

        // Set a realistic user-agent to avoid detection
        await page.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        // Navigate to URL
        await page.goto(process.argv[2], { waitUntil: 'networkidle2' });

        // Simulate human-like interaction
        await page.mouse.move(100, 100);
        await new Promise(resolve => setTimeout(resolve, 2000)); // ✅ Fixed delay method
        await page.mouse.click(100, 100);
        await new Promise(resolve => setTimeout(resolve, 2000)); // ✅ Fixed delay method

        // Wait for Cloudflare challenge (if any)
        await new Promise(resolve => setTimeout(resolve, 5000)); // ✅ Fixed delay method

        // Extract page content
        const content = await page.evaluate(() => document.documentElement.outerHTML);

        // Extract cookies
        const cookies = await page.cookies();

        // ✅ Output ONLY JSON (no logs to interfere with Java process)
        console.log(JSON.stringify({ html: content, cookies }));

    } catch (error) {
        console.error(JSON.stringify({ error: error.message }));
    } finally {
        if (browser) {
            await browser.close();
        }
    }
})();
