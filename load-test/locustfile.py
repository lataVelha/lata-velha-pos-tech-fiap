from locust import HttpUser, task


class FrontUser(HttpUser):
    @task
    def index(self):
        self.client.get("/")
