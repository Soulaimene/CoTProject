const staticCacheName = "site-static-v2"; //change this name for versioning e.g : site-static-v1
const assets = [
    '/',
    '/index.html',
    '/Frontend/js/register.js',
    '/Frontend/css/register.css'
];

// install service worker
self.addEventListener("install",evt => {
    //console.log("service worker has been installed");
    evt.waitUntil(
        caches.open(staticCacheName).then(cache => {
            console.log("caching shell assets");
            cache.addAll(assets);
        }));

});

//activate event:
self.addEventListener("activate",evt => {
// delete the caches (this is good for the cache versioning)
    evt.waitUntil(
        caches.keys().then(keys => {
            return Promise.all(keys
                .filter(key => key !== staticCacheName)
                .map(key => caches.delete(key))
            );
        })
    );
});

//fetch event (this will intercept the requests)
self.addEventListener('fetch',evt => {
    //console.log("fetch event",evt);
    evt.respondWith(
        caches.match(evt.request).then(cacheRes => {
            return cacheRes || fetch(evt.request);
        })
    )
});