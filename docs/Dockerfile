FROM jekyll/jekyll:4.0

COPY Gemfile* /tmp/
WORKDIR /tmp
RUN touch Gemfile.lock \
 && chmod a+w Gemfile.lock \
 && bundle install

RUN mkdir /workdir
WORKDIR /workdir

ENTRYPOINT ["jekyll"]
