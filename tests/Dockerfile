FROM cypress/base:13.8.0

RUN adduser --disabled-password jahians

USER jahians
WORKDIR /home/jahians

COPY --chown=jahians:jahians package.json yarn.lock /home/jahians/

RUN mkdir -p /home/jahians/run-artifacts /home/jahians/results /home/jahians/cypress/plugins

#CI=true reduces the verbosity of the installation logs
RUN CI=true yarn install

# Jahia reporter is used for a couple of methods prior to running the test
RUN yarn add jahia-reporter

COPY --chown=jahians:jahians ./* /home/jahians/

RUN CI=true /home/jahians/node_modules/.bin/cypress install

CMD ["/bin/bash", "-c", "/home/jahians/env.run.sh"]
