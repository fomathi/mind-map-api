db.createUser(
    {
        user: "user-test",
        pwd: "test-password",
        roles: [
            {
                role: "readWrite",
                db: "mind-map"
            }
        ]
    }
);