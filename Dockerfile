FROM python:3.8-slim

ENV PYTHONUNBUFFERED=1

WORKDIR /app
RUN pip install pylint==2.11.1
COPY app app
COPY tests tests

EXPOSE 9000
CMD python factorial.py
